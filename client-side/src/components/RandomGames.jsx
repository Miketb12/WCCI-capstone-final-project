import { useState, useEffect } from "react";
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

function RandomGames() {
  const [randomGames, setRandomGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const MAX_RETRIES = 5; // Maximum number of retries for fetching a game

  useEffect(() => {
    // Define the URL of your backend API to fetch random games
    const apiUrl = "http://localhost:7098/game/detail"; // Adjust the API endpoint as needed

    // Generate 10 random gameIds between 1 and 1000 (adjust the range as needed)
    const getRandomGameId = () => Math.floor(Math.random() * 400000) + 1;

    // Create an array to store the fetched games
    const fetchedGames = [];

    // Function to fetch a game with retries
    const fetchGameWithRetry = async (gameId, retries) => {
      try {
        const response = await fetch(`${apiUrl}/${gameId}`);
        if (!response.ok) {
          throw new Error(`Network response was not ok for gameId ${gameId}`);
        }
        const game = await response.json();
        // Ensure the response is an object with game data
        if (game.id) {
          fetchedGames.push(game);
        } else {
          // Handle cases where the response is unexpected
          throw new Error(`Unexpected API response for gameId ${gameId}`);
        }
      } catch {
        // Retry fetching with a new random gameId if retries remain
        if (retries > 0) {
          const newGameId = getRandomGameId();
          console.warn(`Retrying with a new random gameId: ${newGameId}`);
          await fetchGameWithRetry(newGameId, retries - 1);
        } else {
          // No more retries, mark the game as not found
          fetchedGames.push({
            id: gameId,
            name: "Game Not Found",
            description: "Game not found for this ID.",
          });
        }
      }
    };

    // Fetch details for each random gameId
    const fetchGames = async () => {
      try {
        for (let i = 0; i < 10; i++) {
          const randomGameId = getRandomGameId();
          await fetchGameWithRetry(randomGameId, MAX_RETRIES);
        }
        setRandomGames(fetchedGames);
        setLoading(false);
      } catch (error) {
        setError(error);
        setLoading(false);
      }
    };

    fetchGames();
  }, []);

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
    return <p>Loading...</p>; // You can show a loading indicator here
  }

  if (error) {
    return <p>Error: {error.message}</p>; // Handle the error here
  }

  return (
    <div>
      <div className="carousel-container">
        <div>
          <h2
            style={{
              color: "gold",
              textAlign: "center",
              textDecoration: "underline",
            }}
          >
            Random Games
          </h2>
        </div>
        <Slider {...settings}>
          {randomGames.map((game) => (
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
                  <div>
                    <h2 className="carousel-name">{game.name}</h2>
                  </div>
                </div>
              </div>
            </Link>
          ))}
        </Slider>
      </div>
    </div>
  );
}

export default RandomGames;
