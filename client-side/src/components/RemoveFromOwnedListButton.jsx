import axios from 'axios';
import PropTypes from 'prop-types';

const RemoveFromOwnedListButton = ({ userId, ownedListId, onRemove }) => {
  const handleRemove = () => {
    // Make a DELETE request to remove the item from the wishlist
    axios
      .delete(`http://localhost:7098/user/${userId}/ownedlist/remove/${ownedListId}`)
      .then(() => {
        // If the request is successful, trigger the `onRemove` callback
        onRemove(ownedListId);
        window.location.reload();
      })
      .catch((error) => {
        console.error('Error removing from wishlist:', error);
      });
  };

  return (
    <button onClick={handleRemove} className="RemoveFromListButton">Remove</button>
  );
};

RemoveFromOwnedListButton.propTypes = {
   
    userId: PropTypes.string.isRequired,
    ownedListId: PropTypes.number.isRequired,
    onRemove: PropTypes.func.isRequired,
  };


export default RemoveFromOwnedListButton;


