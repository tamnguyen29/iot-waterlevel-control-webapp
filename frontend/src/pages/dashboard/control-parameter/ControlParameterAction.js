import { useState, Fragment } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import PropTypes from 'prop-types';
import { toast } from 'react-toastify';
import {
  Stack,
  IconButton,
  InputLabel,
  FormHelperText,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Grid,
  OutlinedInput,
  CircularProgress,
  useTheme,
  Typography
} from '@mui/material';
import AnimateButton from 'components/@extended/AnimateButton';
import * as Yup from 'yup';
import { Formik } from 'formik';
import { PlusSquareOutlined, DeleteOutlined, EditOutlined, CloseOutlined } from '@ant-design/icons';
import { createJWTAxios } from 'apis/createInstance';
import services from 'apis/index';

const ControlParameterAction = ({ controlUnit, handleControlUnitChange }) => {
  const theme = useTheme();
  const colors = theme.palette;
  const loginUser = useSelector((state) => state.auth.login.currentUser);
  const dispatch = useDispatch();
  const jwtAxios = createJWTAxios(loginUser, dispatch);
  const [openPopup, setOpenPopup] = useState({
    addControlUnitElement: false,
    deleteControlUnitElement: false,
    editControlUnitElement: false
  });

  const openAddControlUnitElement = () => {
    setOpenPopup((prev) => ({
      ...prev,
      addControlUnitElement: true
    }));
  };

  const openEditControlUnitElement = () => {
    if (controlUnit) {
      setOpenPopup((prev) => ({
        ...prev,
        editControlUnitElement: true
      }));
    } else {
      toast.info('No control parameter chosen!');
    }
  };

  const closeAddControlUnitElement = () => {
    setOpenPopup((prev) => ({
      ...prev,
      addControlUnitElement: false
    }));
  };

  const openDeleteControlUnitElement = () => {
    if (controlUnit) {
      setOpenPopup((prev) => ({
        ...prev,
        deleteControlUnitElement: true
      }));
    } else {
      toast.info('No control parameter chosen!');
    }
  };

  const closeDeleteControlUnitElement = () => {
    setOpenPopup((prev) => ({
      ...prev,
      deleteControlUnitElement: false
    }));
  };
  const closeEditControlUnitElement = () => {
    setOpenPopup((prev) => ({
      ...prev,
      editControlUnitElement: false
    }));
  };
  const AddControlUnitElement = () => {
    return (
      <Dialog open={openPopup.addControlUnitElement} onClose={closeAddControlUnitElement} maxWidth="md">
        <DialogTitle
          sx={{ textAlign: 'center', position: 'relative', textTransform: 'uppercase', color: colors.text.primary, fontSize: '1.5rem' }}
        >
          Create control parameter
          <IconButton style={{ float: 'right' }} onClick={closeAddControlUnitElement}>
            <CloseOutlined style={{ fontSize: '16px', color: colors.primary['main'] }} />
          </IconButton>
        </DialogTitle>
        <DialogContent>
          <Formik
            initialValues={{
              setpoint: 20,
              kp: 1,
              controlName: '',
              submit: null
            }}
            validationSchema={Yup.object().shape({
              setpoint: Yup.number().typeError('Setpoint must be a number!').min(0).max(30).required('Setpoint value is required!'),
              kp: Yup.number().typeError('Kp must be a number!').required('Kp value is required!'),
              controlName: Yup.string().max(200).required('Name of your control parameter is required!')
            })}
            onSubmit={async (values, { setSubmitting }) => {
              setSubmitting(true);
              const controlUnitValue = {
                kp: values.kp,
                name: values.controlName,
                setpoint: values.setpoint,
                userId: loginUser.user.id
              };
              await services.addControlUnit(loginUser.user.id, jwtAxios, controlUnitValue, dispatch);
              setSubmitting(false);
            }}
          >
            {({ errors, handleBlur, handleChange, handleSubmit, isSubmitting, touched, values }) => (
              <form noValidate onSubmit={handleSubmit}>
                <Grid container spacing={3}>
                  <Grid item xs={12}>
                    <Stack spacing={1}>
                      <InputLabel htmlFor="control-name">Name*</InputLabel>
                      <OutlinedInput
                        id="control-name"
                        type="text"
                        value={values.controlName}
                        name="controlName"
                        onBlur={handleBlur}
                        onChange={handleChange}
                        placeholder="Enter control name"
                        fullWidth
                        error={Boolean(touched.controlName && errors.controlName)}
                      />
                      {touched.controlName && errors.controlName && (
                        <FormHelperText error id="standard-weight-helper-kp">
                          {errors.controlName}
                        </FormHelperText>
                      )}
                    </Stack>
                  </Grid>
                  <Grid item xs={12}>
                    <Stack spacing={1}>
                      <InputLabel htmlFor="setpoint-value">Setpoint value*</InputLabel>
                      <OutlinedInput
                        id="setpoint-value"
                        type="number"
                        value={values.setpoint}
                        name="setpoint"
                        onBlur={handleBlur}
                        onChange={handleChange}
                        placeholder="Enter setpoint value"
                        fullWidth
                        error={Boolean(touched.setpoint && errors.setpoint)}
                      />
                      {touched.setpoint && errors.setpoint && (
                        <FormHelperText error id="standard-weight-helper-setpoint">
                          {errors.setpoint}
                        </FormHelperText>
                      )}
                    </Stack>
                  </Grid>

                  <Grid item xs={12}>
                    <Stack spacing={1}>
                      <InputLabel htmlFor="kp-value">Kp value*</InputLabel>
                      <OutlinedInput
                        id="kp-value"
                        type="number"
                        value={values.kp}
                        name="kp"
                        onBlur={handleBlur}
                        onChange={handleChange}
                        placeholder="Enter email address"
                        fullWidth
                        error={Boolean(touched.kp && errors.kp)}
                      />
                      {touched.kp && errors.kp && (
                        <FormHelperText error id="standard-weight-helper-kp">
                          {errors.kp}
                        </FormHelperText>
                      )}
                    </Stack>
                  </Grid>
                  <Grid item xs={12}>
                    <AnimateButton>
                      <Button
                        disableElevation
                        disabled={isSubmitting}
                        fullWidth
                        size="large"
                        type="submit"
                        variant="contained"
                        color="primary"
                      >
                        {isSubmitting ? <CircularProgress color="inherit" size={30} thickness={4} /> : 'Create'}
                      </Button>
                    </AnimateButton>
                  </Grid>
                </Grid>
              </form>
            )}
          </Formik>
        </DialogContent>
      </Dialog>
    );
  };

  const DeleteControlUnitElement = () => {
    const createdTime = new Date(controlUnit?.createdAt);
    const updatedTime = new Date(controlUnit?.updatedAt);
    const formattedDateTime = new Intl.DateTimeFormat('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: 'numeric',
      minute: 'numeric',
      second: 'numeric'
    });
    return (
      controlUnit && (
        <Dialog open={openPopup.deleteControlUnitElement} onClose={closeDeleteControlUnitElement} maxWidth="xs">
          <DialogTitle
            sx={{ textAlign: 'center', position: 'relative', textTransform: 'uppercase', color: colors.text.primary, fontSize: '1.5rem' }}
          >
            Are you sure to delete?
          </DialogTitle>
          <DialogContent sx={{ fontSize: '18px' }}>
            <Typography variant="h4">{controlUnit.name}</Typography>
            <Typography variant="h6">
              [Kp: {controlUnit.kp}, Setpoint: {controlUnit.setpoint}]
            </Typography>
            <Typography variant="h6">Created time: {formattedDateTime.format(createdTime)}</Typography>
            <Typography variant="h6">Updated time: {formattedDateTime.format(updatedTime)}</Typography>
          </DialogContent>
          <DialogActions>
            <Formik
              initialValues={{
                submit: null
              }}
              onSubmit={async (values, { setSubmitting }) => {
                setSubmitting(true);
                await services.deleteControlUnit(loginUser.user.id, jwtAxios, dispatch, controlUnit.id);
                setSubmitting(false);
                closeDeleteControlUnitElement();
                handleControlUnitChange(null);
              }}
            >
              {({ handleSubmit, isSubmitting }) => (
                <form noValidate onSubmit={handleSubmit}>
                  <Stack direction="row" spacing={1}>
                    <AnimateButton>
                      <Button
                        disableElevation
                        disabled={isSubmitting}
                        fullWidth
                        size="small"
                        type="submit"
                        variant="contained"
                        color="primary"
                        sx={{
                          width: '85px',
                          height: '40px',
                          '& .MuiButton-label': {
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center'
                          }
                        }}
                      >
                        {isSubmitting ? (
                          <CircularProgress color="inherit" size={25} thickness={4} />
                        ) : (
                          <span>
                            Confirm <DeleteOutlined />
                          </span>
                        )}
                      </Button>
                    </AnimateButton>
                    <AnimateButton>
                      <Button
                        disableElevation
                        fullWidth
                        size="small"
                        type="button"
                        variant="contained"
                        color="error"
                        sx={{
                          width: '85px',
                          height: '40px',
                          '& .MuiButton-label': {
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center'
                          }
                        }}
                        onClick={closeDeleteControlUnitElement}
                      >
                        Cancel
                      </Button>
                    </AnimateButton>
                  </Stack>
                </form>
              )}
            </Formik>
          </DialogActions>
        </Dialog>
      )
    );
  };
  const EditControlUnitElement = () => {
    return (
      controlUnit && (
        <Dialog open={openPopup.editControlUnitElement} onClose={closeEditControlUnitElement} maxWidth="md">
          <DialogTitle
            sx={{ textAlign: 'center', position: 'relative', textTransform: 'uppercase', color: colors.text.primary, fontSize: '1.5rem' }}
          >
            Edit control parameter
            <IconButton style={{ float: 'right' }} onClick={closeEditControlUnitElement}>
              <CloseOutlined style={{ fontSize: '16px', color: colors.primary['main'] }} />
            </IconButton>
          </DialogTitle>
          <DialogContent>
            <Formik
              initialValues={{
                setpoint: controlUnit.setpoint,
                kp: controlUnit.kp,
                controlName: controlUnit.name
              }}
              validationSchema={Yup.object().shape({
                setpoint: Yup.number().typeError('Setpoint must be a number!').min(0).max(30).required('Setpoint value is required!'),
                kp: Yup.number().typeError('Kp must be a number!').required('Kp value is required!'),
                controlName: Yup.string().max(200).required('Name of your control parameter is required!')
              })}
              onSubmit={async (values, { setSubmitting }) => {
                setSubmitting(true);
                const controlUnitValue = {
                  kp: values.kp,
                  name: values.controlName,
                  setpoint: values.setpoint,
                  userId: loginUser.user.id
                };
                await services.updateControlUnit(loginUser.user.id, jwtAxios, dispatch, controlUnit.id, controlUnitValue);
                setSubmitting(false);
              }}
            >
              {({ errors, handleBlur, handleChange, handleSubmit, isSubmitting, touched, values }) => (
                <form noValidate onSubmit={handleSubmit}>
                  <Grid container spacing={3}>
                    <Grid item xs={12}>
                      <Stack spacing={1}>
                        <InputLabel htmlFor="control-name">Name*</InputLabel>
                        <OutlinedInput
                          id="control-name"
                          type="text"
                          value={values.controlName}
                          name="controlName"
                          onBlur={handleBlur}
                          onChange={handleChange}
                          placeholder={controlUnit.name}
                          fullWidth
                          error={Boolean(touched.controlName && errors.controlName)}
                        />
                        {touched.controlName && errors.controlName && (
                          <FormHelperText error id="standard-weight-helper-kp">
                            {errors.controlName}
                          </FormHelperText>
                        )}
                      </Stack>
                    </Grid>
                    <Grid item xs={12}>
                      <Stack spacing={1}>
                        <InputLabel htmlFor="setpoint-value">Setpoint value*</InputLabel>
                        <OutlinedInput
                          id="setpoint-value"
                          type="number"
                          value={values.setpoint}
                          name="setpoint"
                          onBlur={handleBlur}
                          onChange={handleChange}
                          placeholder={controlUnit.setpoint}
                          fullWidth
                          error={Boolean(touched.setpoint && errors.setpoint)}
                        />
                        {touched.setpoint && errors.setpoint && (
                          <FormHelperText error id="standard-weight-helper-setpoint">
                            {errors.setpoint}
                          </FormHelperText>
                        )}
                      </Stack>
                    </Grid>

                    <Grid item xs={12}>
                      <Stack spacing={1}>
                        <InputLabel htmlFor="kp-value">Kp value*</InputLabel>
                        <OutlinedInput
                          id="kp-value"
                          type="number"
                          value={values.kp}
                          name="kp"
                          onBlur={handleBlur}
                          onChange={handleChange}
                          placeholder={controlUnit.kp}
                          fullWidth
                          error={Boolean(touched.kp && errors.kp)}
                        />
                        {touched.kp && errors.kp && (
                          <FormHelperText error id="standard-weight-helper-kp">
                            {errors.kp}
                          </FormHelperText>
                        )}
                      </Stack>
                    </Grid>
                    <Grid item xs={12}>
                      <AnimateButton>
                        <Button
                          disableElevation
                          disabled={isSubmitting}
                          fullWidth
                          size="large"
                          type="submit"
                          variant="contained"
                          color="primary"
                        >
                          {isSubmitting ? <CircularProgress color="inherit" size={30} thickness={4} /> : 'Update'}
                        </Button>
                      </AnimateButton>
                    </Grid>
                  </Grid>
                </form>
              )}
            </Formik>
          </DialogContent>
        </Dialog>
      )
    );
  };
  return (
    <>
      <Stack direction="row" justifyContent="space-between">
        <IconButton onClick={openAddControlUnitElement}>
          <PlusSquareOutlined style={{ fontSize: '24px' }} />
        </IconButton>
        <IconButton onClick={openDeleteControlUnitElement}>
          <DeleteOutlined style={{ fontSize: '24px' }} />
        </IconButton>
        <IconButton onClick={openEditControlUnitElement}>
          <EditOutlined style={{ fontSize: '24px' }} />
        </IconButton>
      </Stack>
      <Fragment>
        <AddControlUnitElement />
        <DeleteControlUnitElement />
        <EditControlUnitElement />
      </Fragment>
    </>
  );
};
ControlParameterAction.propTypes = {
  controlUnit: PropTypes.any,
  handleControlUnitChange: PropTypes.func
};
export default ControlParameterAction;
