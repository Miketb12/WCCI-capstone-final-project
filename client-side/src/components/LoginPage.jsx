
import { useState } from 'react';
import './LoginPage.module.css';
import { useNavigate } from 'react-router-dom'


const Login = () => {
  const [userName, setUserName] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();
  const url = 'http://localhost:7098/login';

  const handleLogin = async (e) => {
    e.preventDefault();
    // Make an API request to your Spring Boot backend here
    try {
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ userName, password }),
      });

      if (response.ok) {
        const data = await response.json();
        localStorage.setItem('authToken', data.token);
        localStorage.setItem('userId', data.id);
        localStorage.setItem('userName', data.userName);
        console.log('Login User Name: ' +userName)
        console.log('Login successfully');
        
        alert('User Login successfully')
        
        navigate('/user-detail')
        location.reload();
       
      } else {
        console.log("Username or password is incorrect.")
        alert("Username or password is incorrect.")
      }
    } catch (error) {
      console.error('Error: login failed', error);
    }
  };
  

  const handleCreateAccount = async () => {
    navigate("/register");
  }

  return (
    <div className="login-form">
      <h2>Login</h2>
      <form onSubmit={handleLogin}>
      <p>Username</p>
        <input
          type="userName"
          placeholder="UserName"
          value={userName}
          onChange={(e) => setUserName(e.target.value)}
        />
        <p>Password</p>
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
      
      <button type = "Submit" onClick={handleLogin}>Login</button>
      <button type='button' onClick={handleCreateAccount}>Create Account</button>
      </form>
    </div>
  );
}

export default Login;



