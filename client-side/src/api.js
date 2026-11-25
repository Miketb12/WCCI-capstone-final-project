import axios from "axios";
import { handleUnauthorized } from "./auth";

// One axios instance for every call to the Spring Boot server
const api = axios.create({
  baseURL: "http://localhost:7098",
});

api.interceptors.request.use((config) => {
  const authToken = localStorage.getItem("authToken");
  if (authToken) {
    config.headers.Authorization = `Bearer ${authToken}`;
  }
  return config;
});

api.interceptors.response.use(undefined, (error) => {
  if (error.response?.status === 401) {
    handleUnauthorized();
  }
  return Promise.reject(error);
});

export default api;
