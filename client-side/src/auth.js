import axios from "axios";

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

  axios.interceptors.request.use((config) => {
    const authToken = localStorage.getItem("authToken");
    if (authToken) {
      config.headers.Authorization = `Bearer ${authToken}`;
    }
    return config;
  });

  axios.interceptors.response.use(undefined, (error) => {
    if (error.response?.status === 401) {
      handleUnauthorized();
    }
    return Promise.reject(error);
  });
};
