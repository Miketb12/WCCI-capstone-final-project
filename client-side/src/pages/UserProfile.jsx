import { Link } from "react-router-dom";
import { useState, useEffect } from "react";
import RemoveFromWishlistButton from "../components/RemoveFromWishListButton";
import RemoveFromOwnedListButton from "../components/RemoveFromOwnedListButton";
import MoveToOwnedListButton from "../components/MoveToOwnedListButton";
import "../components/Buttons.css";
import { handleUnauthorized } from "../auth";

const UserProfile = () => {
  const [user, setUser] = useState(null);
  const [expandedDescriptions, setExpandedDescriptions] = useState([]);
  const userId = localStorage.getItem("userId");

  const onGameRemoved = (wishListId) => {
    console.log(`Removing game with wishListId: ${wishListId}`);
  };

  const handleItemMoved = (wishListId) => {
    console.log(`Item moved from wish list to owned list: ${wishListId}`);
  };

  useEffect(() => {
    const authToken = localStorage.getItem("authToken");
    const localUserId = localStorage.getItem("userId");

    if (authToken) {
      fetch(`http://localhost:7098/user/id/${localUserId}`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${authToken}`,
        },
      })
        .then((response) => {
          if (response.status === 401) {
            handleUnauthorized();
          }
          if (response.ok) {
            return response.json();
          } else {
            throw new Error("Failed to fetch user details");
          }
        })
        .then((userData) => {
          setUser(userData);
          setExpandedDescriptions(
            new Array(
              userData.ownedLists.length + userData.wishLists.length
            ).fill(false)
          );
        })
        .catch((error) => {
          console.error("Error fetching user details:", error);
        });
    }
  }, []);

  const parseHTML = (html) => {
    const doc = new DOMParser().parseFromString(html, "text/html");
    return doc.documentElement.textContent;
  };

  // Toggle the expansion state of a game description
  const toggleDescriptionExpansion = (index) => {
    const newExpandedDescriptions = [...expandedDescriptions];
    newExpandedDescriptions[index] = !newExpandedDescriptions[index];
    setExpandedDescriptions(newExpandedDescriptions);
  };

  return (
    <div className="container">
      <h2 style={{ color: "gold" }}>User Profile</h2>
      {user ? (
        <>
          <div className="box">
            <p>User ID: {user.id}</p>
            <p>Username: {user.userName}</p>
            <p>Email: {user.email}</p>
          </div>
          <br></br>
          <br></br>
          <br></br>
          <Link to={`/owned-list`}>
            {" "}
            <h2 style={{ color: "gold" }}>My Owned List</h2>
          </Link>
          {user.ownedLists && user.ownedLists.length > 0 ? (
            <div className="box">
              <ul>
                {user.ownedLists.map((ownedItem, index) => (
                  <li key={ownedItem.id} className="game-item">
                    <div className="game-details">
                      <div className="game-image">
                        <img
                          src={ownedItem.game.imgUrl}
                          alt={`Image ${ownedItem.game.name}`}
                        />
                      </div>

                      <div className="game-info game-details-box-2">
                        <Link
                          to={`/game-detail/${ownedItem.game.id}`}
                          key={ownedItem.game.id}
                        >
                          <h4>
                            <b>{ownedItem.game.name}</b>
                          </h4>
                        </Link>
                        {/* <p>Slug: {ownedItem.game.slug}</p> */}
                        <p>
                          <b>Status:</b> Owned
                        </p>
                        {/* <p>Released Date: {ownedItem.game.released}</p> */}
                        <p>
                          {expandedDescriptions[index] ? (
                            <div
                              dangerouslySetInnerHTML={{
                                __html: ownedItem.game.description,
                              }}
                            />
                          ) : (
                            parseHTML(ownedItem.game.description).substring(
                              0,
                              255
                            )
                          )}
                          {ownedItem.game.description.length > 255 && (
                            <button
                              className="read-button"
                              onClick={() => toggleDescriptionExpansion(index)}
                            >
                              {expandedDescriptions[index]
                                ? "Read less"
                                : "......Read more"}
                            </button>
                          )}
                        </p>
                        <div>
                          <RemoveFromOwnedListButton
                            userId={userId}
                            ownedListId={ownedItem.id}
                            onRemove={() => onGameRemoved(ownedItem.id)}
                          />
                        </div>
                      </div>
                    </div>
                  </li>
                ))}
              </ul>
            </div>
          ) : (
            <p style={{ color: "blue" }}>No items in your owned list.</p>
          )}
          <br></br>
          <br></br>
          <br></br>
          <Link to={`/wish-list`}>
            {" "}
            <h2 style={{ color: "gold" }}>My Wish List</h2>
          </Link>
          {user.wishLists && user.wishLists.length > 0 ? (
            <div className="box">
              <ul>
                {user.wishLists.map((wishListItem, index) => (
                  <li key={wishListItem.id} className="game-item">
                    <div className="game-details">
                      <div className="game-image">
                        <img
                          src={wishListItem.game.imgUrl}
                          alt={`Image ${wishListItem.game.name}`}
                        />
                      </div>

                      <div className="game-info game-details-box-2">
                        <Link
                          to={`/game-detail/${wishListItem.game.id}`}
                          key={wishListItem.game.id}
                        >
                          <h4>
                            <b>{wishListItem.game.name}</b>
                          </h4>
                        </Link>
                        {/* <p>Slug: {wishListItem.game.slug}</p> */}
                        {/* <p>Status: {wishListItem.game.status}</p> */}
                        {/* <p>Released Date: {wishListItem.game.released}</p> */}
                        <p>
                          {expandedDescriptions[index] ? (
                            <div
                              dangerouslySetInnerHTML={{
                                __html: wishListItem.game.description,
                              }}
                            />
                          ) : (
                            parseHTML(wishListItem.game.description).substring(
                              0,
                              255
                            )
                          )}
                          {wishListItem.game.description.length > 255 && (
                            <button
                              className="read-button"
                              style={{ color: "gold" }}
                              onClick={() => toggleDescriptionExpansion(index)}
                            >
                              {expandedDescriptions[index]
                                ? "Read less"
                                : "......Read more"}
                            </button>
                          )}
                        </p>
                        <RemoveFromWishlistButton
                          userId={userId}
                          wishListId={wishListItem.id}
                          onRemove={() => onGameRemoved(wishListItem.id)}
                        />
                        <MoveToOwnedListButton
                          userId={userId}
                          wishListId={wishListItem.id}
                          onMove={handleItemMoved}
                        />
                      </div>
                    </div>
                  </li>
                ))}
              </ul>
            </div>
          ) : (
            <p style={{ color: "blue" }}>No items in your wishlist.</p>
          )}
        </>
      ) : (
        <p>Loading user details...</p>
      )}
    </div>
  );
};

export default UserProfile;

// import { Link } from 'react-router-dom';
// import { useState, useEffect } from 'react';

// const UserProfile = () => {
//   const [user, setUser] = useState(null);

//   useEffect(() => {
//     const authToken = localStorage.getItem('authToken');
//     const userId = localStorage.getItem('userId');

//     if (authToken) {
//       fetch(`http://localhost:7098/user/id/${userId}`, {
//         method: 'GET',
//         headers: {
//           'Authorization': `Bearer ${authToken}`,
//         },
//       })
//         .then((response) => {
//           if (response.ok) {
//             return response.json();
//           } else {
//             throw new Error('Failed to fetch user details');
//           }
//         })
//         .then((userData) => {
//           setUser(userData);
//         })
//         .catch((error) => {
//           console.error('Error fetching user details:', error);
//         });
//     }
//   }, []);

//   return (
//     <div className="container">
//       <h2>User Profile</h2>
//       {user ? (
//         <>
//           <div className='box'>
//             <p>User ID: {user.id}</p>
//             <p>Username: {user.userName}</p>
//             <p>Email: {user.email}</p>
//           </div>
//           <br></br>
//           <br></br>
//           <br></br>
//           <h2>My Owned List</h2>
//           {user.ownedlists ? (
//             <div className='box'>
//               <ul>
//                 {user.ownedlists.map((item, index) => (
//                   <li key={index}>{item}</li>
//                 ))}
//               </ul>
//             </div>
//           ) : (
//             <p>No items in your owned list.</p>
//           )}
//           <br></br>
//           <br></br>
//           <br></br>
//           <h2>My Wishlist</h2>
//           {user.wishlists ? (
//             <div className='box'>
//               <ul>
//                 {user.wishlists.map((item, index) => (
//                   <li key={index}>{item}</li>
//                 ))}
//               </ul>
//             </div>
//           ) : (
//             <p>No items in your wishlist.</p>
//           )}
//         </>
//       ) : (
//         <p>Loading user details...</p>
//       )}
//     </div>
//   );
// };

// export default UserProfile;
