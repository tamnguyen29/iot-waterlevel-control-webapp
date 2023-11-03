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

  const connect = () => {
    sock = new SockJS(
      `http://localhost:8080/ws?clientId=${connectClientValue.clientId}&clientType=${connectClientValue.clientType}`
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
        setCurrentUserConnectDeviceId("")
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
    console.log("Handle connect click")
    const message = "Oke"
    stompClient.send(
      `/app/connect-device/${deviceId}`,
      {},
      JSON.stringify(message)
    )
  }

  const DeviceListElement = () => {
    const listDeviceItems = deviceList.map((device, index) => (
      <li key={device.id}>
        {index + 1}, name: {device.name}, connected at: {device.connectedAt}
        {device.usingStatus === "AVAILABLE" ? (
          <button
            type="button"
            onClick={() => handleConnectToDevice(device.id)}
          >
            Using device
          </button>
        ) : (
          <span>
            {connectClientValue.clientId === device.currentUsingUser.id ? (
              <button type="button">Stop using</button>
            ) : (
              <span>
                <b> {device.currentUsingUser.name} is using</b>
              </span>
            )}
          </span>
        )}
      </li>
    ))
    return <ul>{listDeviceItems}</ul>
  }

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
