import axios from "axios";
import { DoctorContext } from "../context/DoctorContext";
import { AdminContext } from "../context/AdminContext";
import { toast } from "react-toastify";
import React, { useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Oval } from "react-loader-spinner";
import { FaTimes } from "react-icons/fa";

const Login = () => {
  const [state, setState] = useState("Admin");
  const backendUrl = import.meta.env.VITE_BACKEND_URL;
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });
  const [loading, setLoading] = useState(false);
  const [otpBar, setOtpBar] = useState(false);
  const [otp, setOtp] = useState(["", "", "", "", "", ""]);
  const [remember, setRemember] = useState(false);
  const { setDToken, setDoctorId } = useContext(DoctorContext);
  const { setAToken, setAdminId } = useContext(AdminContext);

  useEffect(() => {
    const rememberedEmail = localStorage.getItem("rememberedEmail");
    const rememberedPassword = localStorage.getItem("rememberedPassword");

    if (rememberedEmail && rememberedPassword) {
      setFormData({
        email: rememberedEmail,
        password: rememberedPassword,
      });
      setRemember(true);
    }
  }, []);

  function handleChange(e) {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  }

  const handleOtpChange = (e, index) => {
    const value = e.target.value;
    const updatedOtp = [...otp];
    updatedOtp[index] = value;
    setOtp(updatedOtp);
  };

  const onSubmitHandler = async (e) => {
    e.preventDefault();
    console.log("Form submitted:", formData);
    setLoading(true);

    try {
      const { data } = await axios.post(backendUrl + "/api/login", formData);
      const role = data.role;

      if (role === "PATIENT") {
        toast.error("Patient login is not allowed on this portal.");
        return;
      }
    
      
      if (remember) {
        localStorage.setItem("rememberedEmail", formData.email);
        localStorage.setItem("rememberedPassword", formData.password);
      } else {
        localStorage.removeItem("rememberedEmail");
        localStorage.removeItem("rememberedPassword");
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
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="flex flex-col gap-4 m-auto items-start p-8 w-full max-w-md border rounded-xl text-gray-600 text-sm shadow-lg bg-white">
        <h2 className="text-2xl font-semibold m-auto text-gray-800">
          <span className="text-primary">{state}</span> Login
        </h2>

        <form onSubmit={onSubmitHandler} className="w-full space-y-4">
          <div className="w-full">
            <label htmlFor="email" className="block text-gray-700">
              Email
            </label>
            <input
              id="email"
              onChange={handleChange}
              value={formData.email}
              className="border border-gray-300 rounded w-full p-2 mt-1 focus:outline-none focus:ring-2 focus:ring-blue-500"
              type="email"
              name="email"
              autoComplete="current-email"
              required
            />
          </div>

          <div className="w-full">
            <label htmlFor="password" className="block text-gray-700">
              Password
            </label>
            <input
              id="password"
              onChange={handleChange}
              value={formData.password}
              className="border border-gray-300 rounded w-full p-2 mt-1 focus:outline-none focus:ring-2 focus:ring-blue-500"
              type="password"
              name="password"
              required
              autoComplete="current-password"
            />
          </div>

          <div className="flex justify-between items-center w-full">
            <div className="flex items-center">
              <input
                type="checkbox"
                id="remember"
                className="mr-2"
                checked={remember}
                onChange={(e) => setRemember(e.target.checked)}
              />
              <label htmlFor="remember" className="text-gray-600">
                Remember me
              </label>
            </div>
            <a href="/forgotPassword" className="text-blue-600 hover:underline">
              Forgot password?
            </a>
          </div>

          <button
            type="submit"
            className="bg-blue-600 text-white w-full py-2 rounded-md text-base hover:bg-blue-700 transition-colors flex justify-center items-center"
            disabled={loading}
          >
            {loading ? (
              <Oval
                height={20}
                width={20}
                color="white"
                visible={true}
                ariaLabel="oval-loading"
              />
            ) : (
              "Sign In"
            )}
          </button>
        </form>

        {state === "Admin" ? (
          <p className="m-auto text-gray-600">
            Doctor Login?{" "}
            <span
              onClick={() => setState("Doctor")}
              className="text-primary underline cursor-pointer"
            >
              Click here
            </span>
          </p>
        ) : (
          <p className="m-auto text-gray-600">
            Admin Login?{" "}
            <span
              onClick={() => setState("Admin")}
              className="text-primary underline cursor-pointer"
            >
              Click here
            </span>
          </p>
        )}

        {/* OTP Modal */}
        {otpBar && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-xl p-8 max-w-sm w-full relative">
              <button
                onClick={() => setOtpBar(false)}
                className="absolute top-4 right-4 text-gray-500 hover:text-gray-700"
              >
                <FaTimes size={20} />
              </button>

              <h2 className="text-2xl font-medium text-center mb-6">
                Verify your email
              </h2>

              <div className="flex justify-center mb-6">
                <img
                  src="https://cdn-icons-png.flaticon.com/512/3179/3179068.png"
                  alt="Email verification"
                  className="w-20 h-20"
                />
              </div>

              <p className="text-center mb-6 text-gray-600">
                Enter the 6-digit verification code sent to your email
              </p>

              <form className="flex flex-col items-center space-y-4">
                <div className="flex space-x-2">
                  {otp.map((_, index) => (
                    <input
                      key={index}
                      type="text"
                      maxLength="1"
                      value={otp[index]}
                      onChange={(e) => handleOtpChange(e, index)}
                      className="w-12 h-12 text-center text-xl border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  ))}
                </div>

                <button
                  type="submit"
                  className="mt-4 px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
                >
                  Verify
                </button>

                <p className="text-sm text-gray-500">
                  Didn't receive code?{" "}
                  <button className="text-blue-600 hover:underline">
                    Resend
                  </button>
                </p>
              </form>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Login;
