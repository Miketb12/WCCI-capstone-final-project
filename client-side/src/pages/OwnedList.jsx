import { useState, useEffect } from "react";
import api from '../api';
import { Link } from "react-router-dom";
import "../components/ListPage.css";

const OwnedList = () => {
  const [collection, setCollection] = useState([]);

  const authToken = localStorage.getItem("authToken");
  const localUsername = localStorage.getItem("userName");

  useEffect(() => {
    if (authToken) {
      const localUserId = localStorage.getItem("userId");
      const baseUrl = `/user/${localUserId}/ownedlist`;
      api
        .get(baseUrl)
        .then((response) => setCollection(response.data))
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
