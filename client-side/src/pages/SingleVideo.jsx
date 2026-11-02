
const VideoPage = () => {
  const videoUrl =
    "https://media.rawg.io/media/stories-640/cf2/cf2d2ad3d0c3c440ae56dfb77cd21ca6.mp4";
  const gameDetailUrl = "http://localhost:5173/game-detail/22511";

  return (
    <div className="container-gd">

<div className="container-game-details">
    <div className="box-4 video-container" style={{width:'85%'}}>
     
      <a href={gameDetailUrl}>
        <video controls width="800" height="450" loop autoPlay muted>
          <source src={videoUrl} type="video/mp4" />
          Your browser does not support the video tag.
        </video>
      </a>
    </div>
    </div>
    </div>
  );
};

export default VideoPage;
