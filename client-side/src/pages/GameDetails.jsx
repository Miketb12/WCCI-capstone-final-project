import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import AddToListButton from "../components/AddToLisButton";
import './GameDetails.css';


function GameInfoDetails() {
  const { gameId } = useParams();
  const [game, setGame] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [videoUrl, setVideoUrl] = useState(null); // State to store the video URL

  const MAX_RETRIES = 3;
  const userId = localStorage.getItem('userId');
  const authToken = localStorage.getItem('authToken'); 
  const [isModalOpen, setModalOpen] = useState(false);
  


  useEffect(() => {
    const gameAPI = `http://localhost:7098/game/detail/${gameId}`;
    const videoAPI = `http://localhost:7098/game/video/${gameId}`;
    const fetchGameWithRetry = async () => {
      try {
        const response = await fetch(gameAPI);
        if (!response.ok) {
          throw new Error(`Network response was not ok for gameId ${gameId}`);
        }
        const gameData = await response.json();
        if (gameData.id) {
          setGame(gameData);
          setLoading(false);
        } else {
          throw new Error(`Unexpected API response for gameId ${gameId}`);
        }
      } catch (error) {
        
          setError(error);
          setLoading(false);
       
      }
    };

    // Function to fetch the video URL
    const fetchVideoUrl = async () => {
      try {
        const response = await fetch(videoAPI);
        if (!response.ok) {
          throw new Error(`Network response was not ok for gameId ${gameId}`);
        }
        const responseData = await response.json();
    
        console.log("Video API response:", responseData); // Debugging statement
    
        // Ensure the response is an object with the "results" array
        if (responseData.results && responseData.results.length > 0) {
          // Assuming we want to use the first item from the "results" array
          const firstVideo = responseData.results[0];
    
          // Check if the first video item has a "data" property with a "max" URL
          if (firstVideo.data && firstVideo.data.max) {
            setVideoUrl(firstVideo.data.max); // Set the fetched video URL
          } else {
            throw new Error(`No "max" URL found for gameId ${gameId}`);
          }
        } else {
          throw new Error(`No video data found for gameId ${gameId}`);
        }
      } catch (error) {
        console.error(error);
      }
    };

    fetchGameWithRetry(MAX_RETRIES);
    fetchVideoUrl();
  }, [gameId]);

  if (loading) {
    return <p>Loading...</p>;
  }

  if (error) {
    return <p>Error: {error.message}</p>;
  }

  return (
    <div className="container-gd">
      <br />
      <div className="container-game-details">
        <div className="box-1">
          <img src={game.background_image} alt={`Image ${game.name}`} />
        </div>
        <div className="box-2">
          <div className="game-name">
            <h2>{game.name}</h2>
          </div>
          <div className="game-detail">
          <h3>Genre:</h3>{" "}
          <b>
            {game.genres.map((genre, index) => (
              <span key={genre.id}>
                {genre.name}
                {index < game.genres.length - 1 && ", "}
              </span>
            ))}
          </b>
          <br />
          <h3>Release Date:</h3> <b>{game.released}</b>
          <br />
          <h3>Rating:</h3>{" "}
          
          <b>
            {game.rating} / {game.rating_top}
          </b>
            

          <br />
          <h3>Platforms:</h3>
          <div>
            <b>
              {game.platforms.map((platform, index) => (
                <span key={platform.platform.id}>
                  {platform.platform.name}
                  {index < game.platforms.length - 1 ? ", " : ""}
                </span>
              ))}
            </b>
          </div>
          </div>
          

          <div className="button-container-2">
          {authToken && (
            <>
              <AddToListButton gameId={game.id} userId={userId} authToken={authToken}actionType="addToWishList" />
              <AddToListButton gameId={game.id} userId={userId} authToken={authToken}actionType="addToOwnedList" />
            </>
          )}
          </div>
        </div>
        <div className="box-3">
          <b>
            <h1>Description</h1>
          </b>
          <div dangerouslySetInnerHTML={{ __html: game.description }} />
        </div>
        <div className="box-4 video-container">
        {videoUrl ? (
    <iframe
      title={`Video ${game.name}`}
      width="1080"
      height="720"
      src={videoUrl}
      frameBorder="0"
      allowFullScreen
    ></iframe>
  ) : (
   
    <img
      src={game.background_image_additional}
      alt={`Image ${game.name}`}
      onClick={() => setModalOpen(true)}
    />
  )}
        </div>
      </div>
      {isModalOpen && (
        <div className="modal" onClick={() => setModalOpen(false)}>
          <img
            src={game.background_image_additional}
            alt={`Image ${game.name}`}
            onClick={() => setModalOpen(false)}
            
          />
        </div>
      )}
            <br />
    </div>
    
  );
}

export default GameInfoDetails;




  {/* {game.developers.slice(0, 3).map((developer) => (
    <img
      key={developer.id} // Use a unique key
      src={developer.image_background}
      alt={`Developer Image ${developer.name}`}
    />
  ))} */}

          {/* <img src={game.background_image_additional} alt={`Image ${game.name}`} /> */}
          {/* I can use this map below to grab other images in the file. */}
          {/* {game.genres.map((name) => (
            <span key={genre.genre.id}>{platform.platform.name}</span>
          ))} */}
  {/* {game.stores.slice(0, 3).map((store) => (
    <img
      key={store.store.id} // Use a unique key
      src={store.store.image_background}
      alt={`Store Image ${store.store.name}`}
    />
  ))} */}
{
  /* <h1>{game.name}</h1>
      <div className="container">
        <div className="box" key={game.id}>

          <div>
            <h2>Name: {game.name}</h2>
          </div>
          <div className='box-discover-description'>
            <p>Description: {game.description}</p>
          </div>
          <div className='box-discover-rating'>
            <p>Rating: {game.rating} / {game.rating_top}</p>
          </div>
          <div>
            <img src={game.background_image_additional} alt={`Image ${game.name}`} />
          </div>
        </div>
      </div> */
}
