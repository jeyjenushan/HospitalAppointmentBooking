import React, { useContext } from "react";
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
import Login from "./pages/Login";
import Register from "./pages/Register";
import { AppContext } from "./context/AppContext";

const App = () => {
  const { token } = useContext(AppContext);
  const location = useLocation();

  // Check if current route is login or register
  const isAuthPage = ["/login", "/register"].includes(location.pathname);

  // Protected Route Wrapper
  const ProtectedRoutes = () => {
    return token ? <Outlet /> : <Navigate to="/login" replace />;
  };

  return (
    <div className="mx-4 sm:mx-[10%] min-h-screen flex flex-col">
      {!isAuthPage && <Navbar />}
      <main className="flex-grow">
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<Home />} />
          <Route path="/doctors" element={<Doctors />} />
          <Route path="/doctors/:specialization" element={<Doctors />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
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

export default App;
