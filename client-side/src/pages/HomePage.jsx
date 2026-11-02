import SearchBar from '../components/SearchBar';
import NewReleases from '../components/NewReleases'
import RandomGames from '../components/RandomGames';
import DiscoverySearch from '../components/Discovery';
import  SingleVideo from "./SingleVideo";

const HomePage = () => {

  return (
    <div >
    <div className='container-home-page'>
      <div className="search-bar">
      <SearchBar />
      </div>

      <div className='video-1'>
            <SingleVideo/>
      </div>
      
      <div className='new-release'>
        <NewReleases/>

      </div>
      <div className='top-game'>
        <DiscoverySearch/>

      </div>
      <div className='random'>
        <RandomGames/>

      </div>
    </div>
    </div>
    
    
  );
};

export default HomePage;


