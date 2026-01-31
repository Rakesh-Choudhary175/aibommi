import React, { useState, useRef, useEffect } from 'react';
import '../Login.css'; // Reusing global styles for consistency

const ChatWindow = ({ onClose }) => {
  const [messages, setMessages] = useState([
    { id: 1, text: "Hello! I'm your EduPath AI Assistant. How can I help you with your career planning today?", sender: 'ai' }
  ]);
  const [inputText, setInputText] = useState('');
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSendMessage = (e) => {
    e.preventDefault();
    if (!inputText.trim()) return;

    const newUserMessage = {
      id: messages.length + 1,
      text: inputText,
      sender: 'user'
    };

    setMessages(prev => [...prev, newUserMessage]);
    setInputText('');

    // Simulate AI response
    setTimeout(() => {
      const aiResponse = {
        id: messages.length + 2,
        text: `That's an interesting question about "${newUserMessage.text}". Based on the student's performance in Science and Maths, I would suggest exploring fields like Engineering or Data Science.`,
        sender: 'ai'
      };
      setMessages(prev => [...prev, aiResponse]);
    }, 1000);
  };

  return (
    <div className="chat-window">
      <div className="chat-header">
        <h3>AI Career Assistant</h3>
        <button onClick={onClose} className="close-btn">×</button>
      </div>
      
      <div className="chat-messages">
        {messages.map((msg) => (
          <div key={msg.id} className={`message ${msg.sender}`}>
            <div className="message-bubble">
              {msg.text}
            </div>
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>

      <form className="chat-input-area" onSubmit={handleSendMessage}>
        <input
          type="text"
          value={inputText}
          onChange={(e) => setInputText(e.target.value)}
          placeholder="Type your question..."
        />
        <button type="submit">Send</button>
      </form>
    </div>
  );
};

export default ChatWindow;
