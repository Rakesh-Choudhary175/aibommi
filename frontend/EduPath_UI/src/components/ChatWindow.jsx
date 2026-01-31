import React, { useState, useRef, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import '../Login.css'; // Reusing global styles for consistency

const ChatWindow = ({ onClose }) => {
  const { studentId } = useParams();
  const [messages, setMessages] = useState([]);
  const [inputText, setInputText] = useState('');
  const [loading, setLoading] = useState(false);
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages, loading]);

  useEffect(() => {
    const fetchInitialChat = async () => {
      setLoading(true);
      try {
        const response = await axios.post("http://localhost:8080/api/chat", {
          studentId: parseInt(studentId),
          messages: [
            {
              role: "user",
              content: "What should the best carear option for my future? Give output strictly in html format"
            }
          ]
        });
        
        let responseText = response.data;
        if (typeof responseText === 'object') {
             // Prioritize 'reply' field as per new user example
             responseText = responseText.reply || responseText.content || responseText.message || responseText.html || JSON.stringify(responseText);
        }
        
        // Clean markdown code blocks
        if (typeof responseText === 'string') {
          // Remove markdown code block markers
          responseText = responseText.replace(/```html/g, '').replace(/```/g, '');
          
          // Clean literal newlines if they are escaped sequences like "\n" 
          // (The previous user request was specifically about removing "/n" which I interpreted as literal encoded newlines)
          responseText = responseText.replace(/\\n/g, ' '); 

          // Extract body content if present to avoid nested html/head tags
          const bodyMatch = responseText.match(/<body[^>]*>([\s\S]*)<\/body>/i);
          if (bodyMatch && bodyMatch[1]) {
            responseText = bodyMatch[1];
          }
        }

        const aiResponse = {
          id: 1,
          text: responseText,
          sender: 'ai',
          isHtml: true
        };
        setMessages([aiResponse]);
      } catch (error) {
        console.error("Error fetching initial chat", error);
        setMessages([{ id: 1, text: "Failed to load initial chat.", sender: 'ai' }]);
      } finally {
        setLoading(false);
      }
    };

    if (studentId) {
      fetchInitialChat();
    }
  }, [studentId]);

  const handleSendMessage = async (e) => {
    e.preventDefault();
    if (!inputText.trim()) return;

    const userMsgText = inputText;
    const newUserMessage = {
      id: Date.now(),
      text: userMsgText,
      sender: 'user',
      isHtml: false
    };

    setMessages(prev => [...prev, newUserMessage]);
    setInputText('');
    setLoading(true);

    try {
      const response = await axios.post("http://localhost:8080/api/chat", {
        studentId: parseInt(studentId),
        messages: [
          {
            role: "user",
            content: userMsgText + " Give output strictly in html format"
          }
        ]
      });

      let responseText = response.data;
      if (typeof responseText === 'object') {
           responseText = responseText.reply || responseText.content || responseText.message || responseText.html || JSON.stringify(responseText);
      }
      
      // Clean markdown and extract body
      if (typeof responseText === 'string') {
        responseText = responseText.replace(/```html/g, '').replace(/```/g, '');
        responseText = responseText.replace(/\\n/g, ' ');
        
        const bodyMatch = responseText.match(/<body[^>]*>([\s\S]*)<\/body>/i);
        if (bodyMatch && bodyMatch[1]) {
          responseText = bodyMatch[1];
        }
      }

      const aiResponse = {
        id: Date.now() + 1,
        text: responseText,
        sender: 'ai',
        isHtml: true
      };
      setMessages(prev => [...prev, aiResponse]);
    } catch (error) {
      console.error("Chat error", error);
      const errorMsg = {
        id: Date.now() + 1,
        text: "Sorry, I encountered an error processing your request.",
        sender: 'ai',
        isHtml: false
      };
      setMessages(prev => [...prev, errorMsg]);
    } finally {
      setLoading(false);
    }
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
              {msg.isHtml ? (
                <div dangerouslySetInnerHTML={{ __html: msg.text }} />
              ) : (
                msg.text
              )}
            </div>
          </div>
        ))}
        {loading && (
           <div className="message ai">
             <div className="message-bubble">Thinking...</div>
           </div>
        )}
        <div ref={messagesEndRef} />
      </div>

      <form className="chat-input-area" onSubmit={handleSendMessage}>
        <input
          type="text"
          value={inputText}
          onChange={(e) => setInputText(e.target.value)}
          placeholder="Type your question..."
          disabled={loading}
        />
        <button type="submit" disabled={loading}>Send</button>
      </form>
    </div>
  );
};

export default ChatWindow;
