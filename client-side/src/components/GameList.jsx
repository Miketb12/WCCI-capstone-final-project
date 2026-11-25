import { useEffect, useState } from 'react';
import api from '../api';

function GameList() {
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {

    api.get('/game/top_games')
    .then((response) => response.data)
    .then((data) => {
      if (data.results && Array.isArray(data.results)) {
        setGames(data.results);
        setLoading(false);
      } else {
        // Handle cases where the response structure is unexpected
        setError(new Error('Unexpected API response'));
        setLoading(false);
      }
    })
    .catch((error) => {
      setError(error);
      setLoading(false);
    });
}, []);

  // Render games here

  return (
    <div>
    {/* <h2>Game List</h2> */} <br></br>
    {loading ? (
      <p>Loading...</p>
    ) : error ? (
      <p>Error: {error.message}</p>
    ) : (
      <ul>
        {games.map((game) => (
          <li key={game.id}>
          <img src={game.image_background} alt={game.name} />
            <h2>Title: {game.name}</h2>
            <p>Release Date: {game.released}</p>
            <p>Rating: {game.rating}/5</p>
            {/* Add more game details as needed */}
            {/* <h3>Title: {item.name}</h3>
            <p>Slug: {item.slug}</p>
            <p>Released: {item.release}</p>
            <p>Description: {item.description}</p> */}
          </li>
        ))}
      </ul>
    )}
  </div>
  );
}

export default GameList;
