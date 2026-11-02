import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import Slider from "./Slider"; // Import the Slider component
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import "./Carousel.css";
import PropTypes from "prop-types";

function SampleNextArrow(props) {
  const { className, style, onClick } = props;
  return (
    <div
      className={`${className} carousel-arrow-right`}
      style={{ ...style, display: "block" }}
      onClick={onClick}
    />
  );
}

function SamplePrevArrow(props) {
  const { className, style, onClick } = props;
  return (
    <div
      className={`${className} carousel-arrow-left`}
      style={{ ...style, display: "block" }}
      onClick={onClick}
    />
  );
}

SamplePrevArrow.propTypes = {
  className: PropTypes.string,
  style: PropTypes.object, // Validate style as an object
  onClick: PropTypes.func, // Validate onClick as a function
};
SampleNextArrow.propTypes = {
  className: PropTypes.string,
  style: PropTypes.object, // Validate style as an object
  onClick: PropTypes.func, // Validate onClick as a function
};

function NewReleases() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    // Define the URL of your backend API
    const apiUrl = "http://localhost:7098/game/new_games";

    fetch(apiUrl)
      .then((response) => {
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        return response.json();
      })
      .then((data) => {
        if (data.results && Array.isArray(data.results)) {
          setData(data.results);
          setLoading(false);
        } else {
          // Handle cases where the response structure is unexpected
          setError(new Error("Unexpected API response"));
          setLoading(false);
        }
      })
      .catch((error) => {
        setError(error);
        setLoading(false);
      });
  }, []);

  // Configure settings for the carousel
  const settings = {
    dots: false,
    infinite: false,
    speed: 1000,
    slidesToShow: 5, // Number of games to show at once
    slidesToScroll: 1,
    waitForAnimate: false,

    nextArrow: <SampleNextArrow />,
    prevArrow: <SamplePrevArrow />,
  };

  if (loading) {
    return <p>Loading...</p>;
  }

  if (error) {
    return <p>Error: {error.message}</p>;
  }

  return (
    <div className="carousel-container">
      <div>
        <h2
          style={{
            color: "yellow",
            textAlign: "center",
            textDecoration: "underline",
          }}
        >
          New Released Games
        </h2>
      </div>

      <Slider {...settings}>
        {data.map((game) => (
          <Link to={`/game-detail/${game.id}`} key={game.id}>
            <div>
              <div style={{ display: "block", paddingTop: "20px" }}>
                <div>
                  <img
                    src={game.background_image}
                    alt={game.name}
                    className="carousel-item-img"
                  />
                </div>
                <div style={{ display: "block" }}>
                  <h2 className="carousel-name">{game.name}</h2>
                </div>
                <div
                  style={{
                    color: "skyblue",
                    textAlign: "center",
                    padding: "5px",
                  }}
                >
                  <p>
                    Date Released: {""}
                    {game.released}
                  </p>
                </div>

                {/* ... Other content ... */}
              </div>
            </div>
          </Link>
        ))}
      </Slider>
    </div>
  );
}

export default NewReleases;

// import { useEffect, useState } from 'react';
// import { Link } from 'react-router-dom';

// function NewReleases() {
//   const [data, setData] = useState([]);
//   const [loading, setLoading] = useState(true);
//   const [error, setError] = useState(null);

//   useEffect(() => {
//     // Define the URL of your backend API
//     const apiUrl = 'http://localhost:7098/new_games';

//     fetch(apiUrl)
//       .then((response) => {
//         if (!response.ok) {
//           throw new Error('Network response was not ok');
//         }
//         return response.json();
//       })
//       .then((data) => {
//         if (data.results && Array.isArray(data.results)) {
//           setData(data.results);
//           setLoading(false);
//         } else {
//           // Handle cases where the response structure is unexpected
//           setError(new Error('Unexpected API response'));
//           setLoading(false);
//         }
//       })
//       .catch((error) => {
//         setError(error);
//         setLoading(false);
//       });
//   }, []);

//   if (loading) {
//     return <p>Loading...</p>; // You can show a loading indicator here
//   }

//   if (error) {
//     return <p>Error: {error.message}</p>; // Handle the error here
//   }

//   return (
//     <div>
//       <br></br>
//       <br></br>
//       <br></br>
//       <br></br>
//       {/* <h1>HOT GAMES</h1> */}
//       <div>
//         {/* ...the rest of your game listing */}
//       </div>
//       <div className="container">
//         {data.map((game) => (
//           <Link to={`/game-detail/${game.id}`} key={game.id}>
//             <div className="box">
//               <div className="box-image">
//                 <img src={game.background_image} alt={game.name} />
//               </div>
//               <div className="box-name">
//                 <h2>{game.name}</h2>
//               </div>
//               <div className="box-name">
//                 <h2>{game.description}</h2>
//               </div>
//               {/* Render the platform information for each game */}

//               {/* Rest of your game details */}
//                         {/* <div className="box-button">
//             <h2>{game.id}</h2>
//           </div>
//           <div className="box-release">
//             <p><b>Release Date: </b> {game.released}</p>
//           </div>
//           <div className="box-rating">
//             <p><b>Rating: </b> {game.rating}/5</p>
//           </div> */}
//           {/* <div className="box-button">
//             <div className="button-container">
//               Add button to add the game to the WishList
//               <button onClick={() => handleButtonAddWishList(game)}>Add to WishList</button>
//                Add button to add the game to the OwnedList
//               <button onClick={() => handleButtonAddOwnedList(game)}>Add to OwnedList</button>
//             </div>
//           </div> */}
//             </div>
//           </Link>
//         ))}
//       </div>
//       <br></br>
//       <br></br>
//       <br></br>
//       <br></br>
//     </div>
//   );

// }

// export default NewReleases;
