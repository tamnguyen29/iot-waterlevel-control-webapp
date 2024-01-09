import { initializeApp } from 'firebase/app';
import { getStorage } from 'firebase/storage';
const firebaseConfig = {
  apiKey: 'AIzaSyDHvZ9YHdyt8dWTyys0LzN5Xo-PYPcIjlI',
  authDomain: 'waterlevel-control-webapp.firebaseapp.com',
  projectId: 'waterlevel-control-webapp',
  storageBucket: 'waterlevel-control-webapp.appspot.com',
  messagingSenderId: '478339274844',
  appId: '1:478339274844:web:9ae8528531419b6123ee99',
  measurementId: 'G-B5SY2LV6SF'
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
export const storage = getStorage(app);
