import { useState, useEffect } from "react";

const WishList = () => {
  const [collection, setCollection] = useState([]);
  const baseUrl = 'http://localhost:7098/wish-list';

  useEffect(() => {
    const fetchWishListData = async () => {
      try {
        const response = await fetch(baseUrl);
        if (!response.ok) {
          throw new Error('Response is not right');
        }
        const data = await response.json();
        setCollection(data);
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };

    fetchWishListData();
  }, []);

  return (
    <div>
    <br></br>
    <br></br>
    <br></br>
    <br></br>
    {/* <h1>HOT GAMES</h1> */}
    <div className="container">
      {collection.map((item) => (
        <div className="box" key={item.id}>
        <div className="box-image">
        <img src={item.imgUrl} alt={`Image ${item.name}`} />
          </div>
          <div className="box-name">
          <h2>Title: {item.name}</h2>
          </div>
          <div className="box-release">
          <p><b>Slug: </b>{item.slug}</p>
          </div>
          <div className="box-rating">
          <p><b>Released: </b>{item.release}</p>
          </div>
          <div className="box-name">
          <p><b>Description: </b> {item.description}</p>
          </div>
        </div>
      ))}
    </div>
    <br></br>
    <br></br>
    <br></br>
    <br></br>
  </div>
    // <div>
    //   <h1>WishList</h1>
    //   <div className="container">
    //     {collection.map((item) => (
    //       <div className="box" key={item.id}>
            // <img
            //   src={item.imgUrl}
            //   alt={`Image ${item.name}`}
            // />
    //         <h3>Title: {item.name}</h3>
    //         <p>Slug: {item.slug}</p>
    //         <p>Released: {item.release}</p>
    //         <p>Description: {item.description}</p>
    //         {/* Add more item details as needed */}
    //       </div>
    //     ))}
    //   </div>
    // </div>
  );
};

export default WishList;
