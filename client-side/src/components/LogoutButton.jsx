const buttonStyle = {
  backgroundColor: "transparent",
  border: "none",
  cursor: "pointer",
  color: "white",
  transition: "color 0.3s",
  width: "135px",
};

const hoverColor = "gold";

const LogoutButton = () => {
  const handleLogout = () => {
    // Perform logout logic here, such as clearing user data from localStorage
    localStorage.removeItem("authToken");
    localStorage.removeItem("userName");
    localStorage.removeItem("userId");
    alert("You have Logout!");
  };

  const handleMouseEnter = (event) => {
    // Apply hover color when the mouse enters the button
    event.target.style.color = hoverColor;
  };

  const handleMouseLeave = (event) => {
    // Restore the default style when the mouse leaves the button
    event.target.style.color = buttonStyle.color;
  };

  return (
    <button
      style={buttonStyle}
      onMouseEnter={handleMouseEnter}
      onMouseLeave={handleMouseLeave}
      onClick={handleLogout}
    >
      <a href="http://localhost:5173/" style={{ textAlign: "center" }}>
        Logout
      </a>
    </button>
  );
};

export default LogoutButton;
