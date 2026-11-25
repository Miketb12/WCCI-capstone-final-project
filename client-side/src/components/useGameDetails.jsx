import { useState, useEffect } from 'react';
import api from '../api';

function useGameDetails(gameId) {
  const [game, setGame] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    // Define the URL of your backend API to fetch game details
    const apiUrl = `/game-detail/${gameId}`;

    // Function to fetch game details
    const fetchGameDetails = async () => {
      try {
        const { data: gameData } = await api.get(apiUrl);
        setGame(gameData);
        setLoading(false);
      } catch (error) {
        setError(error);
        setLoading(false);
      }
    };

    fetchGameDetails();
  }, [gameId]);

  return { game, loading, error };
}

export default useGameDetails;
