import React, { useContext, useMemo } from "react";
import Navbar from "./components/Navbar";
import { Routes, Route, Navigate, useLocation, Outlet } from "react-router-dom";
import Home from "./pages/Home";
import Doctors from "./pages/Doctors";
import About from "./pages/About";
import Contact from "./pages/Contact";
import Appointment from "./pages/Appointment";
import MyAppointments from "./pages/MyAppointments";
import MyProfile from "./pages/MyProfile";
import Footer from "./components/Footer";
import Verify from "./pages/Verify";
import ForgotPassword from "./pages/ForgotPassword";

import { AppContext } from "./context/AppContext";
import Register from "./pages/Register/Register";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import Login from "./pages/Login/Login";

const App = () => {
  const location = useLocation();

  // Memoize the auth page check
  const isAuthPage = useMemo(
    () =>
      ["/login", "/register", "/forgotPassword"].includes(location.pathname),
    [location.pathname]
  );

  return (
    <div className="mx-4 sm:mx-[10%] min-h-screen flex flex-col">
      <ToastContainer
        position="top-right"
        autoClose={5000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="light"
      />
      {!isAuthPage && <Navbar />}
      <main className="flex-grow">
        <Routes>
          {/* Public Routes */}
          <Route path="/register" element={<Register />} />
          <Route path="/login" element={<Login />} />

          

          <Route path="/" element={<Home />} />
          <Route path="/doctors" element={<Doctors />} />
          <Route path="/doctors/:specialization" element={<Doctors />} />

          <Route path="/about" element={<About />} />
          <Route path="/contact" element={<Contact />} />
          <Route path="/forgotPassword" element={<ForgotPassword />} />

          {/* Protected Routes */}
          <Route element={<ProtectedRoutes />}>
            <Route path="/appointment/:docId" element={<Appointment />} />
            <Route path="/my-appointments" element={<MyAppointments />} />
            <Route path="/my-profile" element={<MyProfile />} />
            <Route path="/verify" element={<Verify />} />
          </Route>

          {/* Catch-all route */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
      {!isAuthPage && <Footer />}
    </div>
  );
};

// Move ProtectedRoutes outside to prevent recreation on each render
const ProtectedRoutes = () => {
  const { token } = useContext(AppContext);
  const location = useLocation();

  if (!token) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return <Outlet />;
};

export default React.memo(App);
