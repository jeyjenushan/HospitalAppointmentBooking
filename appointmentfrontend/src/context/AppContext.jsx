import { createContext, useEffect, useState } from "react";
import { toast } from "react-toastify";
import axios from "axios";
import { useNavigate } from "react-router-dom";

export const AppContext = createContext();

const AppContextProvider = (props) => {
  const currencySymbol = "$";
  const backendUrl = import.meta.env.VITE_BACKEND_URL;
  const [userData, setUserData] = useState(null);
  const [docInfo, setDocInfo] = useState(null);
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const [patientId, setPatientId] = useState(
    localStorage.getItem("patientId") ? localStorage.getItem("patientId") : ""
  );
  const [token, setToken] = useState(
    localStorage.getItem("token") ? localStorage.getItem("token") : ""
  );

  //REGISTER PATIENT
  const registerPatient = async (formDataToSend, image) => {
    setLoading(true);
    try {
      const { data } = await axios.post(
        backendUrl + "/api/auth/patient",
        formDataToSend,
        { headers: { "Content-Type": "multipart/form-data" } }
      );

      if (data.statusCode === 200) {
        toast.success("Registration successful! Please login.😀");
        navigate("/login");
      }
    } catch (err) {
      console.log(err.message);
      console.log(err.response?.data?.message);
      const errorMessage = "Registration failed Please try again😔";
      toast.error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  //LOGIN PATIENT
  const loginPatient = async (formDataToSend, remember) => {
    setLoading(true);
    try {
      const { data } = await axios.post(
        backendUrl + "/api/login",
        formDataToSend
      );

      if (data.statusCode === 200) {
        if (remember) {
          localStorage.setItem("rememberedEmail", formDataToSend.email);
          localStorage.setItem("rememberedPassword", formDataToSend.password);
        } else {
          localStorage.removeItem("rememberedEmail");
          localStorage.removeItem("rememberedPassword");
        }

        setToken(data.token);
        setPatientId(data.userDto.patientId);
        localStorage.setItem("token", data.token);
        localStorage.setItem("patientId", data.userDto.patientId);

        toast.success("Login successful!");
        navigate("/");
      } else {
        throw new Error(data.message || "Login failed");
      }
    } catch (err) {
      const errorMessage =
        err.response?.data?.message || err.message || "Login failed";
      toast.error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  //SEND OTP
  const sendOtp = async (email) => {
    try {
      const { data } = await axios.post(
        `${backendUrl}/api/forgotpassword/send-otp`,
        {},
        {
          params: { email },
        }
      );
      if (data.statusCode == 200) {
        toast.success(data.message);
        return true;
      } else {
        toast.error(data.message || "Failed to send otp your email"); // Display error if doctorDtos is missing
        return false;
      }
    } catch (error) {
      // Log error for debugging and show error message to the user
      console.error(error);
      toast.error(
        error.response?.data?.message ||
          "An error occurred while sending email to the user"
      );
      return false;
    }
  };

  //VERIFY OTP
  const verifyOtp = async (email, otp) => {
    try {
      const { data } = await axios.post(
        `${backendUrl}/api/forgotpassword/verify-otp`,
        {},
        {
          params: { email, otp },
        }
      );
      if (data.statusCode == 200) {
        toast.success(data.message);
        return true;
      } else {
        toast.error(data.message || "Wrong OTP provided"); // Display error if doctorDtos is missing
        return false;
      }
    } catch (error) {
      console.error(error);
      toast.error(
        error.response?.data?.message || "An error occurred verify  otp"
      );
      return false;
    }
  };

  //RESET PASSWORD
  const resetPassword = async (email, otp, newPassword) => {
    try {
      const { data } = await axios.post(
        `${backendUrl}/api/forgotpassword/reset-password`,
        {},
        {
          params: { email, newPassword, otp },
        }
      );
      if (data.statusCode == 200) {
        toast.success(data.message);
        return true;
      } else {
        toast.error(data.message || "Wrong OTP provided");
        return false;
        // Display error if doctorDtos is missing
      }
    } catch (error) {
      console.error(error);
      toast.error(error.response?.data?.message || "Error resetting password");
      return false;
    }
  };

  // Getting User Profile using API
  const loadUserProfileData = async () => {
    try {
      const { data } = await axios.get(
        backendUrl + `/api/patients/${patientId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (data.statusCode == 200) {
        setUserData(data.patientDto);
      } else {
        toast.error(data.message);
      }
    } catch (error) {
      console.log(error);
      toast.error(error.message);
    }
  };

  useEffect(() => {
    if (token) {
      loadUserProfileData();
    }
  }, [token]);

  // Getting Doctors using API
  const getDoctosData = async () => {
    try {
      const { data } = await axios.get(backendUrl + "/api/doctors");
      if (data.statusCode == 200) {
        setDoctors(data.doctorDtos);
      } else {
        toast.error(data.message);
      }
    } catch (error) {
      console.log(error);
      toast.error(error.message);
    }
  };

  const fetchDocInfo = async (docId) => {
    try {
      const { data } = await axios.get(
        `${backendUrl}/api/doctors/getDoctor/${docId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (data.statusCode == 200) {
        setDocInfo(data.doctorDto);
      } else {
        toast.error(data.message);
      }
    } catch (error) {
      console.log(error);
      toast.error(error.message);
    }
  };

  /*
  useEffect(() => {
    getDoctosData();
  }, []);*/
  /*

*/
  const value = {
    registerPatient,
    loginPatient,
    sendOtp,
    verifyOtp,
    resetPassword,

    

    doctors,
    getDoctosData,
    currencySymbol,
    backendUrl,
    token,
    setToken,
    userData,
    setUserData,
    loadUserProfileData,
    setPatientId,
    patientId,
    docInfo,
    setDocInfo,
    fetchDocInfo,
  };

  return (
    <AppContext.Provider value={value}>{props.children}</AppContext.Provider>
  );
};

export default AppContextProvider;
