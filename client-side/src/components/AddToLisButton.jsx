import { useState, useEffect } from 'react';
import api from '../api';
import PropTypes from 'prop-types';

function AddToListButton({ gameId, userId, authToken, actionType }) {
  const [isAdded, setIsAdded] = useState(false);
  const [isInOwnedList, setIsInOwnedList] = useState(false);
  const [isInWishList, setIsInWishList] = useState(false);

  useEffect(() => {
    // Check if the game is already in the owned list
    if (actionType === 'addToOwnedList') {
      api
        .get(`/user/check-in-lists/${userId}/${gameId}`)
        .then((response) => {
          setIsInOwnedList(response.data.isInOwnedList);
        })
        .catch((error) => {
          console.error(error);
        });
    }

    // Check if the game is already in the wish list
    if (actionType === 'addToWishList') {
      api
        .get(`/user/check-in-lists/${userId}/${gameId}`)
        .then((response) => {
          setIsInWishList(response.data.isInWishList);
          setIsInOwnedList(response.data.isInOwnedList);
        })
        .catch((error) => {
          console.error(error);
        });
    }
  }, [gameId, userId, authToken, actionType]);

  const handleAction = () => {
    if (isInOwnedList) {
      alert('This game is already in your Owned List.');
      return;
    }else if (isInWishList) {
      alert('This game is already in your Wish List')
      return;
    }

    const endpoint =
      actionType === 'addToWishList'
        ? `/user/add-to-wish-list/${gameId}?userId=${userId}`
        : `/user/add-to-owned-list/${gameId}?userId=${userId}`;

    // Make an API request to add the game to the corresponding list
    api
      .post(endpoint, {})
      .then((response) => {
        if (response.status === 200) {
          setIsAdded(true);
        }
      })
      .catch((error) => {
        console.error(error);
      });
  }

  return (
    <div>
      {isAdded ? (
        <div className='box-button-added'>Added to {actionType === 'addToWishList' ? 'WishList' : 'OwnedList'}</div>
      ) : (
        <button onClick={handleAction}>
          Add to {actionType === 'addToWishList' ? 'Wish List' : 'OwnedList'}
        </button>
      )}
    </div>
  );
}

AddToListButton.propTypes = {
  gameId: PropTypes.number.isRequired,
  userId: PropTypes.string.isRequired,
  authToken: PropTypes.string.isRequired,
  actionType: PropTypes.oneOf(['addToWishList', 'addToOwnedList']).isRequired,
};

export default AddToListButton;
