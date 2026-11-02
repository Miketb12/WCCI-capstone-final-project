import { useState, useEffect } from 'react';
import axios from 'axios';
import PropTypes from 'prop-types';
import { useSearchParams } from 'react-router-dom';
import './SearchResultsPage.css'
import SearchBar from '../components/SearchBar';
import { Link } from 'react-router-dom';

function SearchResults( ) {
  const [result, setResult] = useState({ query: null, results: [], error: null });
  
  const [searchParams] = useSearchParams();
  const query = searchParams.get('query');
  const isCurrent = result.query === query;
  const loading = Boolean(query) && !isCurrent;
  const error = isCurrent ? result.error : null;
  const searchResults = isCurrent ? result.results : [];

  useEffect(() => {
    if (!query) {
      return;
    }
    let ignore = false; // Drop the response if a newer search has started.

    axios.get(`http://localhost:7098/game/search?query=${encodeURIComponent(query)}`)
      .then((response) => {
        if (!ignore) {
          setResult({ query, results: response.data.results, error: null });
        }
      })
      .catch((error) => {
        if (!ignore) {
          setResult({ query, results: [], error });
        }
      });

    return () => {
      ignore = true;
    };
  }, [query]);

  return (
    <div>
      <div className="search-bar">
        <SearchBar />
      </div>
      <div className="search-results-container">
        <h3 style={{ color: 'gold', marginBottom: '30px', marginTop: '30px'}}>Search Results:</h3>
        {loading ? (
          <p>Loading...</p>
        ) : error ? (
          <p>Error: {error.message}</p>
        ) : (
          <ul className="search-results-list">
            {searchResults.map((result) => (
              
              <Link to={`/game-detail/${result.id}`  }key={result.id}>
              <li className="search-result-item">
              <div className="result-item-content">
                <img src={result.background_image} alt={result.name} className='result-img' 
                />
                <div className="result-text" >
                <p style={{color:'purple'}}><b>{result.name} </b></p>
          <p>ID: {result.id}</p>
          
          
          <p>Released Date: {result.released}</p>
        </div>
                </div>
              </li>
              </Link>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}

SearchResults.propTypes = {
  query: PropTypes.string,
};

export default SearchResults;

