import { useState } from 'react';
import { useNavigate } from 'react-router-dom'

const SignupPage = () => {
  const [userName, setUserName] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [email, setEmail] = useState('');
  const url = 'http://localhost:7098/register';
  const navigate = useNavigate();

  const handleSignup = async () => {
    try {
      // Perform client-side validation
      if (password !== confirmPassword) {
        console.error('Passwords do not match');
        alert('Passwords do not match');
        return;
      }

      // Make a POST request to your backend API to create a new user account
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({userName, password, email }),
      });

      if (response.ok) {
        console.log('Registration successful');
        alert("You have registered successful, please login!")
        navigate('/login')
      } else {
        // Handle registration failure, show error message to the user
        console.error('Registration failed');
        alert("registration failed! Please try again.")
      }
    } catch (error) {
      console.error('An error occurred during registration', error);
    }
  };

  const handleCancel = () => {
    // You can define the behavior for canceling here, e.g., redirect to a different page.
    navigate('/'); // Change the path as needed.
  };

  return (
    <div>
      <br></br>
      <br></br>
      <form className='login-form'>
      <h1>Sign Up</h1>
      <p>Username</p>
        <input
          type="userName"
          placeholder="Username"
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
        <input
          type="password"
          placeholder="Confirm Password"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
        />
        <p>Email</p>
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <button type="button" onClick={handleSignup}>
          Sign Up
        </button>
        <button type="button" onClick={handleCancel}>
          Cancel
        </button>
      </form>
    </div>
  );
};

export default SignupPage;
