import axios from 'axios';
import PropTypes from 'prop-types';
import './Buttons.css'

const MoveToOwnedListButton = ({ userId, wishListId, onMove }) => {
  const handleMoveToOwnedList = () => {
    // Make a POST request to move the item from wish list to owned list
    axios
      .post(`http://localhost:7098/user/${userId}/moveToOwnedList/${wishListId}`)
      .then(() => {
        // If the request is successful, trigger the `onMove` callback
        onMove(wishListId);
        window.location.reload();
      })
      .catch((error) => {
        console.error('Error moving item from wish list to owned list:', error);
      });
  };

  return (
    <button onClick={handleMoveToOwnedList} className='RemoveFromListButton'>Move to Owned</button>
  );
};

MoveToOwnedListButton.propTypes = {
    userId: PropTypes.string.isRequired,
    wishListId: PropTypes.number.isRequired,
    onMove: PropTypes.func.isRequired,
};

export default MoveToOwnedListButton;

