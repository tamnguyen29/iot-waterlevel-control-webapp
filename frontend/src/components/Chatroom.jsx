// import React, { useState } from "react"
// import { over } from "stompjs"
// import SockJS from "socketjs-client"

// const initialValue = {
//   userName: "",
//   receiverName: "",
//   isConnected: false,
//   message: "",
// }

// var stompClient = null
// const Chatroom = () => {
//   const [userData, setUserData] = useState(initialValue)
//   const [publicChats, setPublicChats] = useState([])
//   const [privateChats, setPrivateChats] = useState(new Map())
//   const [tab, setTab] = useState("CHAT_ROOM")

//   console.log("Code rendered")

//   const handleChangeUserData = event => {
//     const { value, name } = event.target
//     setUserData(prevData => ({
//       ...prevData,
//       [name]: value,
//     }))
//   }

//   const handleRegisterUser = () => {
//     let Sock = new SockJS("http://localhost:8080/ws")
//     stompClient = over(Sock)
//     stompClient.connect({}, onConnected, onError)
//   }

//   const onConnected = () => {
//     setUserData(prevData => ({
//       ...prevData,
//       isConnected: true,
//     }))
//     stompClient.subscribe("/chatroom/public", onPublicMessageReceived)
//     stompClient.subscribe(
//       "/user/" + userData.userName + "private",
//       onPrivateMessageReceived
//     )
//     userJoin()
//   }

//   const userJoin = () => {
//     if (stompClient) {
//       let chatMessage = {
//         senderName: userData.userName,
//         status: "JOIN",
//         date: Date.now(),
//       }
//       stompClient.send("/app/message", {}, JSON.stringify(chatMessage))
//     }
//   }

//   const onError = error => {
//     console.log(error)
//   }

//   const onPublicMessageReceived = payload => {
//     let payloadData = JSON.parse(payload.body)
//     switch (payloadData.status) {
//       case "JOIN":
//         if (!privateChats.get(payloadData.senderName)) {
//           privateChats.set(payloadData.senderName, [])
//           setPrivateChats(new Map(privateChats))
//         }
//         break
//       case "MESSAGE":
//         publicChats.push(payloadData)
//         setPublicChats([...publicChats])
//         break
//       case "LEAVE":
//         break
//       default:
//         break
//     }
//   }

//   const onPrivateMessageReceived = payload => {
//     let payloadData = JSON.parse(payload.body)
//     if (payloadData.get(payloadData.senderName)) {
//       privateChats.get(payloadData.senderName).push(payloadData)
//       setPrivateChats(new Map(privateChats))
//     } else {
//       let privateRoom = []
//       privateRoom.push(payloadData)
//       privateChats.set(payload.senderName, privateRoom)
//       setPrivateChats(new Map(privateChats))
//     }
//   }

//   const handleSendPublicMessage = () => {
//     if (stompClient) {
//       let chatMessage = {
//         senderName: userData.userName,
//         message: userData.message,
//         status: "MESSAGE",
//         date: Date.now(),
//       }
//       stompClient.send("/app/message", {}, JSON.stringify(chatMessage))
//       setUserData({ ...setUserData, message: "" })
//     }
//   }

//   const handleSendPrivateMessage = () => {
//     if (stompClient) {
//       let chatMessage = {
//         senderName: userData.userName,
//         receiverName: tab,
//         message: userData.message,
//         status: "MESSAGE",
//         date: Date.now(),
//       }
//       if (userData.userName !== tab) {
//         privateChats.get(tab).push(chatMessage)
//         setPrivateChats(new Map(privateChats))
//       }

//       stompClient.send("/app/private-message", {}, JSON.stringify(chatMessage))
//       setUserData({ ...userData, message: "" })
//     }
//   }

//   const GroupChat = () => {
//     return (
//       <div className="chat-content">
//         <ul className="chat-messages">
//           {publicChats.map((chat, index) => {
//             <li className="message" key={index}>
//               {chat.senderName !== userData.userName && (
//                 <div className="avatar">{chat.senderName}</div>
//               )}
//               <div className="message-data">{chat.message}</div>
//               {chat.senderName === userData.userName && (
//                 <div className="avatar self">{chat.senderName}</div>
//               )}
//             </li>
//           })}
//         </ul>

//         <div className="send-message">
//           <input
//             name="message"
//             type="text"
//             className="input-message"
//             placeholder="Enter message to group"
//             value={userData.message}
//             onChange={handleChangeUserData}
//           />
//           <button
//             className="send-button"
//             type="button"
//             onClick={handleSendPublicMessage}
//           >
//             Send
//           </button>
//         </div>
//       </div>
//     )
//   }

//   const PrivateChat = () => {
//     return (
//       <div className="chat-content">
//         <ul className="chat-messages">
//           {[...privateChats.get(tab)].map((chat, index) => {
//             ;<li className="message" key={index}>
//               {chat.senderName !== userData.userName && (
//                 <div className="avatar">{chat.senderName}</div>
//               )}
//               <div className="message-data">{chat.message}</div>
//               {chat.senderName === userData.userName && (
//                 <div className="avatar self">{chat.senderName}</div>
//               )}
//             </li>
//           })}
//         </ul>

//         <div className="send-message">
//           <input
//             name="message"
//             type="text"
//             className="input-message"
//             placeholder={`Send message to ${tab}`}
//             value={userData.message}
//             onChange={handleMessage}
//           />
//           <button
//             className="send-button"
//             type="button"
//             onClick={handleSendPrivateMessage}
//           >
//             Send
//           </button>
//         </div>
//       </div>
//     )
//   }

//   const ChatRoom = () => {
//     return (
//       <div className="chat-box">
//         <MemberList />
//         {tab === "CHAT_ROOM" && <GroupChat />}
// 			{tab !== "CHAT_ROOM" && <PrivateChat />}
// 			<div>
				
// 		</div>
//       </div>
//     )
//   }

//   const Register = () => {
//     return (
      // <div className="register">
      //   <input
      //     id="userName"
      //     name="userName"
      //     type="text"
      //     placeholder="Enter user your username"
      //     value={userData.userName}
      //     onChange={handleChangeUserData}
      //   />
      //   <button type="button" onClick={handleRegisterUser}>
      //     Connect
      //   </button>
      // </div>
//     )
//   }

//   return (
//     <div className="container">
//       {!userData.isConnected ? <Register /> : <ChatRoom />}
//     </div>
//   )
// }

// const MemberList = () => {
//   return (
//     <div className="member-list">
//       <ul>
//         <li
//           onClick={setTab("CHAT_ROOM")}
//           className={`member ${tab === "CHAT_ROOM" && "active"}`}
//         >
//           Chatroom
//         </li>
//         {[...privateChats.keys()].map((name, index) => {
//           ;<li
//             className={`member ${tab === name && "active"}`}
//             key={index}
//             onClick={setTab(name)}
//           >
//             {name}
//           </li>
//         })}
//       </ul>
//     </div>
//   )
// }

// export default Chatroom
