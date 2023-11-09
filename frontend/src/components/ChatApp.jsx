import { useState } from "react"
import { over } from "stompjs"
import SockJS from "sockjs-client/dist/sockjs"

var stompClient = null
var sock = null

const App = () => {
  const [isConnected, setIsConnected] = useState(false)
  const [connectClientValue, setConnectClientValue] = useState({
    clientId: "",
    clientType: "USER",
  })
  const [userList, setUserList] = useState([])
  const [deviceList, setDeviceList] = useState([])
  const [currentUserConnectDeviceId, setCurrentUserConnectDeviceId] =
    useState("")
  const [loading, setLoading] = useState(false)

  const [waterLevelData, setWaterLevelData] = useState({
    value: "",
    time: "",
  })
  const [deviceControllerId, setDeviceControllerId] = useState("")

  const connect = () => {
    sock = new SockJS(
      `http://192.168.1.6:8080/ws?clientId=${connectClientValue.clientId}&clientType=${connectClientValue.clientType}`
    )
    stompClient = over(sock)
    stompClient.connect({}, onConnected, onError)
  }

  const onConnected = () => {
    if (connectClientValue.clientType === "USER") {
      stompClient.subscribe("/topic/members", onMembersMessageReceived)
      stompClient.subscribe(
        "/client/queue/user-private",
        onPrivateMessageReceived
      )
    }

    if (connectClientValue.clientType === "DEVICE") {
      stompClient.subscribe(
        "/client/queue/device-private",
        onDeviceMessageReceived
      )
    }
    stompClient.subscribe("/client/queue/error", onErrorMessageReceived)
    setIsConnected(true)
    console.log("Connected")

    userConnect()
  }

  const userConnect = () => {
    stompClient.send("/app/member-connect", {}, null)
  }

  const onError = (e) => {
    setIsConnected(false)
    console.log("On Error: " + e)
  }

  const onMembersMessageReceived = (payload) => {
    console.log("Member body received: ", payload.body)
    const receivedMessage = JSON.parse(payload.body)

    switch (receivedMessage.action) {
      case "SEND_LIST_CONNECTED_USER":
        setUserList(receivedMessage.content)
        break
      case "SEND_LIST_CONNECTED_DEVICE":
        setDeviceList(receivedMessage.content)
        break
      default:
        break
    }
  }

  const onPrivateMessageReceived = (payload) => {
    const receivedMessage = JSON.parse(payload.body)
    switch (receivedMessage.action) {
      case "SEND_LIST_CONNECTED_DEVICE":
        setDeviceList(receivedMessage.content)
        break
      case "USER_CONNECT_TO_DEVICE":
        //Address when connect here
        setLoading(false)
        break
      case "USER_DISCONNECT_TO_DEVICE":
        break
      case "SEND_WATER_LEVEL_DATA":
        setWaterLevelData({
          value: receivedMessage.content,
          time: receivedMessage.time,
        })
        break
      default:
        break
    }
  }

  const onDeviceMessageReceived = (payload) => {
    const receivedMessage = JSON.parse(payload.body)
    console.log("Device received message: ", receivedMessage)
    switch (receivedMessage.action) {
      case "USER_CONNECT_TO_DEVICE":
        setCurrentUserConnectDeviceId(receivedMessage.content)
        break
      case "USER_DISCONNECT_TO_DEVICE":
        setCurrentUserConnectDeviceId("User disconnect")
        break
      case "START_MEASUREMENT":
        console.log(receivedMessage)
        setDeviceControllerId(receivedMessage.content)
        break
      case "STOP_MEASUREMENT":
        setDeviceControllerId("STOP")
        break
    }
  }

  const onErrorMessageReceived = (payload) => {
    const errorMessage = JSON.parse(payload.body)
    console.log("Error message: ", errorMessage)
  }

  const handleConnectServer = () => {
    connect()
  }

  const handleDisconnect = () => {
    if (stompClient && stompClient.connected) {
      if (connectClientValue.clientType === "USER") {
        stompClient.unsubscribe("/topic/members")
        stompClient.unsubscribe("/client/queue/user-private")
      }

      if (connectClientValue.clientType === "DEVICE") {
        stompClient.unsubscribe("/topic/members")
        stompClient.unsubscribe("/client/queue/device-private")
      }

      stompClient.unsubscribe("/queue/error")
      // Disconnect the Stomp client
      stompClient.disconnect(() => {
        console.log("Disconnected")
        stompClient = null
      })
      sock.close()
      setIsConnected(false)
    }
  }

  const handleConnectChange = (event) => {
    const { value, name } = event.target
    setConnectClientValue((prev) => ({
      ...prev,
      [name]: value,
    }))
  }

  const handleConnectToDevice = (deviceId) => {
    setLoading(true)
    stompClient.send(`/app/connect-device/${deviceId}`, {}, null)
  }

  const handleDisConnectToDevice = (deviceId) => {
    stompClient.send(`/app/stop-connect-device/${deviceId}`, {}, null)
  }

  const startMeasurement = (deviceId) => {
    const controllerId = "653369ff0d9f084480a71faf"
    stompClient.send(
      `/app/operation/start-measurement/${deviceId}`,
      {},
      controllerId
    )
  }

  const stopMeasurement = (deviceId) => {
    stompClient.send(`/app/operation/stop-measurement/${deviceId}`, {}, null)
  }

  const DeviceListElement = () => {
    const listDeviceItems = deviceList.map((device, index) => (
      <li key={device.id}>
        {index + 1}, name: {device.name}, connected at: {device.connectedAt}
        {device.usingStatus === "AVAILABLE" ? (
          <span>
            {!loading ? (
              <button
                type="button"
                onClick={() => handleConnectToDevice(device.id)}
              >
                Using device
              </button>
            ) : (
              <span> Loading</span>
            )}
          </span>
        ) : (
          <span>
            {connectClientValue.clientId === device.currentUsingUser.id ? (
              <div>
                <button
                  type="button"
                  onClick={() => handleDisConnectToDevice(device.id)}
                >
                  Stop using
                </button>
                <div>
                  <p>Controller: 653369ff0d9f084480a71faf</p>
                  <button
                    type="button"
                    onClick={() => startMeasurement(device.id)}
                  >
                    Start
                  </button>
                  <button
                    type="button"
                    onClick={() => stopMeasurement(device.id)}
                  >
                    Stop
                  </button>
                </div>
                <p>
                  Current water level: {waterLevelData.value}, time:{" "}
                  {waterLevelData.time}
                </p>
              </div>
            ) : (
              <span>
                <b> {device.currentUsingUser.name} is using</b>
              </span>
            )}
          </span>
        )}
        <div></div>
      </li>
    ))
    return <ul>{listDeviceItems}</ul>
  }

  // const handleClick = () => {
  //   stompClient.send(
  //     "/app/send-water-level-data",
  //     {},
  //     JSON.stringify({
  //       value: "12",
  //       controlUnitId: "1232131231",
  //       userId: "12323",
  //     })
  //   )
  // }

  return (
    <div>
      <div className="connect-disconnect-container">
        <input
          id="clientId"
          name="clientId"
          type="text"
          value={connectClientValue.clientId}
          onChange={(e) => handleConnectChange(e)}
        />
        <p> {JSON.stringify(connectClientValue)} </p>
        <select
          name="clientType"
          id="clientType"
          onChange={handleConnectChange}
        >
          <option value="USER">USER</option>
          <option value="DEVICE">DEVICE</option>
        </select>
        <div className="button-container">
          <button type="button" onClick={handleConnectServer}>
            Connect Server
          </button>
          <br />
          <button type="button" onClick={handleDisconnect}>
            Disconnect Server
          </button>
        </div>
      </div>

      {isConnected ? (
        <div>
          <p>Server connected</p>
          <br />

          {connectClientValue.clientType === "USER" ? (
            <div>
              <p>List user connect</p>
              <p>{JSON.stringify(userList)}</p>
              <br />
              <p>List device</p>
              {deviceList.length > 0 ? (
                <DeviceListElement />
              ) : (
                <p>No devices connect right now!</p>
              )}
            </div>
          ) : (
            <div>
              <p>This is ESP32</p>
              <p>
                Current user connect to this ESP32: {currentUserConnectDeviceId}
              </p>
              <p>Controller: {deviceControllerId}</p>
            </div>
          )}

          <br />
        </div>
      ) : (
        <p>Server is not connected</p>
      )}
    </div>
  )
}

export default App
