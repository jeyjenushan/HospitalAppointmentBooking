import axios from "axios";
import { DoctorContext } from "../../context/DoctorContext";
import { AdminContext } from "../../context/AdminContext";
import { toast } from "react-toastify";
import React, { useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Oval } from "react-loader-spinner";
import { FaTimes } from "react-icons/fa";
import LoginForm from "../../components/Login/LoginForm";

const Login = () => {
  const [state, setState] = useState("Admin");

  const [loading, setLoading] = useState(false);
  const [otpBar, setOtpBar] = useState(false);
  const [otp, setOtp] = useState(["", "", "", "", "", ""]);
  const [remember, setRemember] = useState(false);

  const handleOtpChange = (e, index) => {
    const value = e.target.value;
    const updatedOtp = [...otp];
    updatedOtp[index] = value;
    setOtp(updatedOtp);
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="flex flex-col gap-4 m-auto items-start p-8 w-full max-w-md border rounded-xl text-gray-600 text-sm shadow-lg bg-white">
        <h2 className="text-2xl font-semibold m-auto text-gray-800">
          <span className="text-primary">{state}</span> Login
        </h2>

        <LoginForm />

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
