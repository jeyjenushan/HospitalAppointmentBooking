import axios from "axios";
import { DoctorContext } from "../context/DoctorContext";
import { AdminContext } from "../context/AdminContext";
import { toast } from "react-toastify";
import React, { useContext, useState } from "react";
import { useNavigate } from "react-router-dom";

const Login = () => {
  const [state, setState] = useState("Admin");
  const backendUrl = import.meta.env.VITE_BACKEND_URL;
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });

  const { setDToken, setDoctorId } = useContext(DoctorContext);
  const { setAToken, setAdminId } = useContext(AdminContext);

  function handleChange(e) {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  }

  const onSubmitHandler = async (e) => {
    e.preventDefault();
    console.log("Form submitted:", formData);

    try {
      const { data } = await axios.post(backendUrl + "/api/login", formData);

      const role = data.role;

      if (role === "PATIENT") {
        toast.error("Patient login is not allowed on this portal.");
        return;
      }

      if (role === "ADMIN") {
        setAToken(data.token);
        setAdminId(data.userDto.adminId);
        localStorage.setItem("aToken", data.token);
        navigate("/admin-dashboard");
      } else if (role === "DOCTOR") {
        setDToken(data.token);
        setDoctorId(data.userDto.doctorId);
        localStorage.setItem("dToken", data.token);
        navigate("/doctor-dashboard");
      } else {
        toast.error("Unauthorized role.");
      }
    } catch (err) {
      console.error(err);
      toast.error("Login failed. Please check your credentials.");
    }
  };

  return (
    <form onSubmit={onSubmitHandler} className="min-h-[80vh] flex items-center">
      <div className="flex flex-col gap-3 m-auto items-start p-8 min-w-[340px] sm:min-w-96 border rounded-xl text-[#5E5E5E] text-sm shadow-lg">
        <p className="text-2xl font-semibold m-auto">
          <span className="text-primary">{state}</span> Login
        </p>
        <div className="w-full ">
          <p>Email</p>
          <input
            onChange={handleChange}
            value={formData.email}
            className="border border-[#DADADA] rounded w-full p-2 mt-1"
            type="email"
            name="email"
            autoComplete="current-email"
            required
          />
        </div>
        <div className="w-full ">
          <label htmlFor="password" className="block">
            Password
          </label>
          <input
            id="password"
            onChange={handleChange}
            value={formData.password}
            className="border border-[#DADADA] rounded w-full p-2 mt-1"
            type="password"
            name="password"
            required
            autoComplete="current-password"
          />
        </div>
        <button className="bg-primary text-white w-full py-2 rounded-md text-base">
          Login
        </button>
        {state === "Admin" ? (
          <p>
            Doctor Login?{" "}
            <span
              onClick={() => setState("Doctor")}
              className="text-primary underline cursor-pointer"
            >
              Click here
            </span>
          </p>
        ) : (
          <p>
            Admin Login?{" "}
            <span
              onClick={() => setState("Admin")}
              className="text-primary underline cursor-pointer"
            >
              Click here
            </span>
          </p>
        )}
      </div>
    </form>
  );
};

export default Login;
