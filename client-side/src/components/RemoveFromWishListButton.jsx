import axios from 'axios';
import PropTypes from 'prop-types';

const RemoveFromWishlistButton = ({ userId, wishListId, onRemove }) => {
  const handleRemove = () => {
    // Make a DELETE request to remove the item from the wishlist
    axios
      .delete(`http://localhost:7098/user/${userId}/wishlist/remove/${wishListId}`)
      .then(() => {
        // If the request is successful, trigger the `onRemove` callback
        onRemove(wishListId);
        window.location.reload();
      })
      .catch((error) => {
        console.error('Error removing from wishlist:', error);
      });
  };

  return (
    <button onClick={handleRemove} className='RemoveFromListButton'>Remove</button>
  );
};

RemoveFromWishlistButton.propTypes = {
   
    userId: PropTypes.number.isRequired,
    wishListId: PropTypes.number.isRequired,
    onRemove: PropTypes.func.isRequired,
  };


export default RemoveFromWishlistButton;


