import { ref, uploadBytes, getDownloadURL, deleteObject } from 'firebase/storage';
import { storage } from './firebaseConfig';
import { v4 } from 'uuid';

export const uploadImage = async (image, defaultUrl) => {
  if (!image) return defaultUrl;

  const imageRef = ref(storage, `images/${image.name + '_' + v4()}`);

  try {
    const snapshot = await uploadBytes(imageRef, image);
    const downloadURL = await getDownloadURL(snapshot.ref);
    return downloadURL;
  } catch (error) {
    return;
  }
};

export const deleteImage = async (imageUrl) => {
  if (!imageUrl) return;
  try {
    const desertRef = ref(storage, imageUrl);
    await deleteObject(desertRef);
  } catch (error) {
    return;
  }
};
