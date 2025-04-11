import React, { useContext, useEffect, useState } from "react";
import { AppContext } from "../context/AppContext";
import axios from "axios";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";

const Login = () => {
  const [state, setState] = useState("Sign Up"); // Default to Sign Up
  const navigate = useNavigate();

  const { setToken, setPatientId, backendUrl } = useContext(AppContext);

  // Form input state
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [age, setAge] = useState("");
  const [gender, setGender] = useState("");
  const [contactNumber, setContactNumber] = useState("");
  const [address, setAddress] = useState("");
  const [medicalHistory, setMedicalHistory] = useState("");
  const [image, setImage] = useState(null);

  const onSubmitHandler = async (e) => {
    e.preventDefault();

    // Form data for Sign Up
    const patientObj = {
      age: age,
      gender: gender,
      contactNumber,
      address,
      medicalHistory,
      user: {
        name: name,
        email: email,
        password: password,
      },
    };

    // Create FormData for image and other patient details
    const formData = new FormData();
    formData.append("patient", JSON.stringify(patientObj));
    formData.append("image", image);

    try {
      // Handle Sign Up or Login based on current state
      const response =
        state === "Sign Up"
          ? await axios.post(backendUrl + "/api/auth/patient", formData, {
              headers: { "Content-Type": "multipart/form-data" },
            })
          : await axios.post(backendUrl + "/api/login", { email, password });

      const { data } = response;
      console.log(data);

      if (data.statusCode === 200) {
        // Successful response handling
        if (state === "Sign Up") {
          //  toast.success("Registration successful! Please log in.");
          setTimeout(() => {
            setState("Sign In"); // Redirect to login page after sign up
          }, 2000); // Redirect to login page after sign up
        } else {
          // Successful login
          setToken(data.token); // Store token in context
          setPatientId(data.userDto.patientId);
          localStorage.setItem("patientId", data.userDto.patientId); // Store patient ID in context
          localStorage.setItem("token", data.token); // Store token in localStorage
          toast.success("Login successful!");
          navigate("/"); // Redirect to homepage after successful login
        }
      } else {
        // Error handling if statusCode is not 200
        throw new Error(data.message || "Unknown error");
      }
    } catch (err) {
      // Handle errors in API call
      const errorMessage =
        err.response && err.response.data
          ? err.response.data.message
          : err.message || "An error occurred during the request.";

      toast.error(
        `${
          state === "Sign Up" ? "Registration" : "Login"
        } failed: ${errorMessage}`
      );
    }
  };

  return (
    <form onSubmit={onSubmitHandler} className="min-h-[80vh] flex items-center">
      <div className="flex flex-col gap-3 m-auto items-start p-8 min-w-[340px] sm:min-w-96 border rounded-xl text-zinc-600 text-sm shadow-lg">
        <p className="text-2xl font-semibold">
          {state === "Sign Up" ? "Create Account" : "Login"}
        </p>
        <p>
          Please {state === "Sign Up" ? "sign up" : "log in"} to book
          appointment
        </p>

        {state === "Sign Up" && (
          <>
            <div className="w-full">
              <p>Full Name</p>
              <input
                onChange={(e) => setName(e.target.value)}
                value={name}
                className="border border-zinc-300 rounded w-full p-2 mt-1"
                type="text"
                required
              />
            </div>
            <div className="w-full">
              <p>Age</p>
              <input
                onChange={(e) => setAge(e.target.value)}
                value={age}
                className="border border-zinc-300 rounded w-full p-2 mt-1"
                type="number"
                required
              />
            </div>
            <div className="w-full">
              <p>Gender</p>
              <select
                onChange={(e) => setGender(e.target.value)}
                value={gender}
                className="border border-zinc-300 rounded w-full p-2 mt-1"
                required
              >
                <option value="">Select Gender</option>
                <option value="Male">Male</option>
                <option value="Female">Female</option>
                <option value="Other">Other</option>
              </select>
            </div>
            <div className="w-full">
              <p>Contact Number</p>
              <input
                onChange={(e) => setContactNumber(e.target.value)}
                value={contactNumber}
                className="border border-zinc-300 rounded w-full p-2 mt-1"
                type="text"
                required
              />
            </div>
            <div className="w-full">
              <p>Address</p>
              <input
                onChange={(e) => setAddress(e.target.value)}
                value={address}
                className="border border-zinc-300 rounded w-full p-2 mt-1"
                type="text"
                required
              />
            </div>
            <div className="w-full">
              <p>Medical History</p>
              <textarea
                onChange={(e) => setMedicalHistory(e.target.value)}
                value={medicalHistory}
                className="border border-zinc-300 rounded w-full p-2 mt-1"
                required
              />
            </div>
            <div className="w-full">
              <p>Upload Image</p>
              <input
                type="file"
                onChange={(e) => setImage(e.target.files[0])}
                className="border border-zinc-300 rounded w-full p-2 mt-1"
              />
            </div>
          </>
        )}
        <div className="w-full ">
          <p>Email</p>
          <input
            onChange={(e) => setEmail(e.target.value)}
            value={email}
            className="border border-zinc-300 rounded w-full p-2 mt-1"
            type="email"
            required
          />
        </div>
        <div className="w-full ">
          <p>Password</p>
          <input
            onChange={(e) => setPassword(e.target.value)}
            value={password}
            className="border border-zinc-300 rounded w-full p-2 mt-1"
            type="password"
            required
          />
        </div>
        <button className="bg-primary text-white w-full py-2 rounded-md text-base">
          {state === "Sign Up" ? "Create Account" : "Login"}
        </button>
        {state === "Sign Up" ? (
          <p>
            Already have an account?{" "}
            <span
              onClick={() => setState("Login")}
              className="text-primary underline cursor-pointer"
            >
              Login here
            </span>
          </p>
        ) : (
          <p>
            Create a new account?{" "}
            <span
              onClick={() => setState("Sign Up")}
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
