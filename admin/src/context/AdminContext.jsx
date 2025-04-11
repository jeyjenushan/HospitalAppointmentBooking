import axios from "axios";
import { createContext, useState } from "react";
import { toast } from "react-toastify";

export const AdminContext = createContext();

const AdminContextProvider = (props) => {
  const [aToken, setAToken] = useState(
    localStorage.getItem("aToken") ? localStorage.getItem("aToken") : ""
  );
  const backendUrl = import.meta.env.VITE_BACKEND_URL;

  const [adminId, setAdminId] = useState(0);
  const [appointments, setAppointments] = useState([]);
  const [doctors, setDoctors] = useState([]);
  const [dashData, setDashData] = useState(null);

  // Getting all Doctors data from Database using API
  const getAllDoctors = async () => {
    try {
      const { data } = await axios.get(backendUrl + "/api/doctors"); // Send GET request to the backend

      if (data && data.doctorDtos) {
        // Check if data and doctorDtos are availableA
        setDoctors(data.doctorDtos); // Update the state with doctors data
      } else {
        toast.error(data.message || "Failed to fetch doctors data"); // Display error if doctorDtos is missing
      }
    } catch (error) {
      // Log error for debugging and show error message to the user
      console.error(error);
      toast.error(
        error.response?.data?.message ||
          "An error occurred while fetching doctors"
      );
    }
  };

  // Function to change doctor availablity using API
  const changeAvailability = async (docId) => {
    try {
      const { data } = await axios.put(
        `${backendUrl}/api/doctors/${docId}/availability`,
        {},
        {
          headers: {
            Authorization: `Bearer ${aToken}`, // Use Bearer scheme if that's the expected format
          },
        }
      );
      if (data.statusCode == 200) {
        toast.success(data.message);
        getAllDoctors();
      } else {
        toast.error(data.message);
      }
    } catch (error) {
      console.log(error);
      toast.error(error.message);
    }
  };

  // Getting all appointment data from Database using API
  const getAllAppointments = async () => {
    try {
      const { data } = await axios.get(backendUrl + "/api/appointments", {
        headers: {
          Authorization: `Bearer ${aToken}`, // Use Bearer scheme if that's the expected format
        },
      });

      if (data.statusCode === 200) {
        setAppointments(data.appointmentDtos.reverse());
      } else {
        toast.error(data.message || "Failed to fetch appointments");
      }
    } catch (error) {
      toast.error(error.message || "Something went wrong");
      console.log(error);
    }
  };

  // Function to cancel appointment using API
  const cancelAppointment = async (appointmentId) => {
    try {
      const { data } = await axios.post(
        `${backendUrl}/api/appointments/${appointmentId}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${aToken}`, // Use Bearer scheme if that's the expected format
          },
        }
      );

      if (data.statusCode == 200) {
        toast.success(data.message);
        getAllAppointments();
      } else {
        toast.error(data.message || "Failed to fetch appointments");
      }
    } catch (error) {
      toast.error(error.message || "Something went wrong");
      console.log(error);
    }
  };

  // Getting Admin Dashboard data from Database using API
  const getDashData = async () => {
    try {
      const { data } = await axios.get(backendUrl + "/api/admin/dashboard", {
        headers: {
          Authorization: `Bearer ${aToken}`, // Use Bearer scheme if that's the expected format
        },
      });

      if (data.statusCode == 200) {
        setDashData(data.dashboardData);
        console.log(data.dashboardData);
      } else {
        toast.error(data.message || "Failed to load dashboard data");
      }
    } catch (error) {
      console.log(error);
      toast.error(error.message || "Something went wrong");
    }
  };

  const value = {
    adminId,
    setAdminId,
    aToken,
    setAToken,
    doctors,
    getAllDoctors,
    changeAvailability,
    appointments,
    getAllAppointments,
    cancelAppointment,
    getDashData,
    dashData,
  };

  return (
    <AdminContext.Provider value={value}>
      {props.children}
    </AdminContext.Provider>
  );
};

export default AdminContextProvider;
