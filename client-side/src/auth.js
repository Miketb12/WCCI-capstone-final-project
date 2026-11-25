const clearSession = () => {
  localStorage.removeItem("authToken");
  localStorage.removeItem("userName");
  localStorage.removeItem("userId");
};

// The token is missing or expired, so the user has to log in again
export const handleUnauthorized = () => {
  clearSession();
  window.location.assign("/login");
};

export const setupAuth = () => {
  // Older logins stored the text "undefined" because the server sent no token
  if (localStorage.getItem("authToken") === "undefined") {
    clearSession();
  }
};
