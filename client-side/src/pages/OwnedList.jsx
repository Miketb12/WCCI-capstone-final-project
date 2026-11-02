import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import "../components/ListPage.css";
import { handleUnauthorized } from "../auth";

const OwnedList = () => {
  const [collection, setCollection] = useState([]);

  const authToken = localStorage.getItem("authToken");
  const localUsername = localStorage.getItem("userName");

  useEffect(() => {
    if (authToken) {
      const localUserId = localStorage.getItem("userId");
      const baseUrl = `http://localhost:7098/user/${localUserId}/ownedlist`;
      fetch(baseUrl, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${authToken}`,
        },
      })
        .then((response) => {
          if (response.status === 401) {
            handleUnauthorized();
          }
          if (!response.ok) {
            throw new Error("Response is not right");
          }
          return response.json();
        })
        .then(setCollection)
        .catch((error) => {
          console.error("Error fetching data:", error);
        });
    }
  }, [authToken]);

  return (
    <div>
      <h1 style={{ color: "orange" }}>{localUsername + "'s"} Owned List</h1>
      <div className="list-container">
        {collection.map((item) => (
          <Link
            to={`/game-detail/${item.game.id}`}
            key={item.id}
            className="nav-link"
          >
            <div className="container-box" key={item.id}>
              <div style={{ padding: "1px" }}>
                <div>
                  <img
                    src={item.game.imgUrl}
                    alt={`Image ${item.name}`}
                    className="container-box-img"
                  />
                </div>
              </div>
              <div className="container-font">
                <p>Title: {item.game.name}</p>

                <p>Released: {item.game.released}</p>
              </div>
            </div>
          </Link>
        ))}
      </div>
    </div>
  );
};

export default OwnedList;
