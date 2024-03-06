import PropTypes from 'prop-types';
import { useState, Fragment, useRef } from 'react';
import { useDispatch, useSelector } from 'react-redux';
// material-ui
import { useTheme } from '@mui/material/styles';
import DefaultAvatar from 'assets/images/users/default_avatar.png';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  IconButton,
  Button,
  Box,
  Grid,
  Avatar,
  Card,
  CardContent,
  Divider,
  Typography,
  CardActions,
  Stack,
  FormHelperText,
  CardHeader,
  TextField,
  CircularProgress
} from '@mui/material';

import { CloseOutlined, ProfileOutlined } from '@ant-design/icons';
import * as Yup from 'yup';
import { Formik } from 'formik';
import services from 'apis/index';
import { createJWTAxios } from 'apis/createInstance';

const EditProfileElement = ({ openEditProfile, handleCloseEditProfile }) => {
  const fileInputRef = useRef(null);
  const theme = useTheme();
  const colors = theme.palette;
  const loginUser = useSelector((state) => state.auth.login.currentUser);
  const dispatch = useDispatch();
  const jwtAxios = createJWTAxios(loginUser, dispatch);
  const avatar = loginUser.user.avatar === 'DEFAULT' ? DefaultAvatar : loginUser.user.avatar;
  const [displayImage, setDisplayImage] = useState(avatar);
  const imageUploadRef = useRef(null);
  const handleUploadPictureClick = () => {
    fileInputRef.current.click();
  };

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    if (!selectedFile) return;
    const reader = new FileReader();
    reader.onload = (event) => {
      setDisplayImage(event.target.result);
    };
    reader.readAsDataURL(selectedFile);
    imageUploadRef.current = selectedFile;
  };
  return (
    <Fragment>
      <Dialog open={openEditProfile} onClose={handleCloseEditProfile} maxWidth="md" fullWidth>
        <DialogTitle
          sx={{ textAlign: 'center', position: 'relative', textTransform: 'uppercase', color: colors.text.primary, fontSize: '1.5rem' }}
        >
          Edit your information <ProfileOutlined />
          <IconButton style={{ float: 'right' }} onClick={handleCloseEditProfile}>
            <CloseOutlined style={{ fontSize: '16px', color: colors.primary['main'] }} />
          </IconButton>
        </DialogTitle>
        <DialogContent>
          <Grid container spacing={3}>
            <Grid item xs={12} md={6} lg={3}>
              <Card>
                <CardContent>
                  <Box
                    sx={{
                      alignItems: 'center',
                      display: 'flex',
                      flexDirection: 'column'
                    }}
                  >
                    <Avatar
                      src={displayImage}
                      sx={{
                        height: 80,
                        mb: 2,
                        width: 80
                      }}
                    />
                    <Typography gutterBottom variant="h5">
                      {loginUser.user.fullName}
                    </Typography>
                    <Typography color="text.secondary" variant="body2">
                      {loginUser.user.role === 'ROLE_ADMIN' ? 'ADMIN' : 'USER'}
                    </Typography>
                  </Box>
                </CardContent>
                <Divider />
                <CardActions>
                  <input
                    type="file"
                    ref={fileInputRef}
                    style={{ display: 'none' }}
                    onChange={handleFileChange}
                    accept="image/jpeg, image/png"
                  />
                  <Button fullWidth variant="text" onClick={handleUploadPictureClick}>
                    Upload picture
                  </Button>
                </CardActions>
              </Card>
            </Grid>
            <Grid item xs={12} md={6} lg={9}>
              <Formik
                initialValues={{
                  fullName: loginUser.user.fullName,
                  email: loginUser.user.email,
                  phoneNumber: loginUser.user.phoneNumber
                }}
                validationSchema={Yup.object().shape({
                  fullName: Yup.string().max(100).required('Your full name is required!'),
                  email: Yup.string().email('Must be a valid email').max(255).required('Email is required'),
                  phoneNumber: Yup.string().matches('(\\+61|0)[0-9]{9}', 'Not valid phone number').required('Phone number is required!')
                })}
                onSubmit={async (values, { setSubmitting }) => {
                  setSubmitting(true);
                  const newInfo = { ...values, imageFile: imageUploadRef.current };
                  await services.updateInfoUser(jwtAxios, dispatch, loginUser, newInfo);
                  setSubmitting(false);
                }}
              >
                {({ errors, handleBlur, handleChange, handleSubmit, isSubmitting, touched, values }) => (
                  <form noValidate onSubmit={handleSubmit}>
                    <Card>
                      <CardHeader subheader="The information can be edited" title="Profile" />
                      <CardContent>
                        <Grid container spacing={2}>
                          <Grid item xs={12} md={6} lg={6}>
                            <Stack spacing={1}>
                              <TextField
                                id="full-name"
                                label="Full name*"
                                type="text"
                                value={values.fullName}
                                name="fullName"
                                onBlur={handleBlur}
                                onChange={handleChange}
                                placeholder="Enter your full name"
                                fullWidth
                                error={Boolean(touched.fullName && errors.fullName)}
                              />
                              {touched.fullName && errors.fullName && (
                                <FormHelperText error id="standard-weight-helper-fullName">
                                  {errors.fullName}
                                </FormHelperText>
                              )}
                            </Stack>
                          </Grid>
                          <Grid item xs={12} md={6} lg={6}>
                            <Stack spacing={1}>
                              <TextField
                                id="email"
                                label="Email*"
                                type="text"
                                value={values.email}
                                name="email"
                                onBlur={handleBlur}
                                onChange={handleChange}
                                placeholder="Edit your email"
                                fullWidth
                                error={Boolean(touched.email && errors.email)}
                              />
                              {touched.email && errors.email && (
                                <FormHelperText error id="standard-weight-helper-email">
                                  {errors.email}
                                </FormHelperText>
                              )}
                            </Stack>
                          </Grid>
                          <Grid item xs={12} md={6} lg={6}>
                            <Stack spacing={1}>
                              <TextField
                                id="phone-number"
                                label="Phone number*"
                                type="text"
                                value={values.phoneNumber}
                                name="phoneNumber"
                                onBlur={handleBlur}
                                onChange={handleChange}
                                placeholder="Edit your phone number"
                                fullWidth
                                error={Boolean(touched.phoneNumber && errors.phoneNumber)}
                              />
                              {touched.phoneNumber && errors.phoneNumber && (
                                <FormHelperText error id="standard-weight-helper-phone-number">
                                  {errors.phoneNumber}
                                </FormHelperText>
                              )}
                            </Stack>
                          </Grid>
                        </Grid>
                      </CardContent>
                      <Divider />
                      <CardActions sx={{ justifyContent: 'flex-end' }}>
                        <Button type="submit" variant="contained" disabled={isSubmitting} sx={{ width: '120px' }}>
                          {isSubmitting ? <CircularProgress color="inherit" size={25} thickness={4} /> : 'Save details'}
                        </Button>
                      </CardActions>
                    </Card>
                  </form>
                )}
              </Formik>
            </Grid>
          </Grid>
        </DialogContent>
      </Dialog>
    </Fragment>
  );
};
EditProfileElement.propTypes = {
  openEditProfile: PropTypes.bool,
  handleCloseEditProfile: PropTypes.func
};

export default EditProfileElement;
