import React from "react";
import Header from "../../components/Home/Header";
import SpecialityMenu from "../../components/Home/SpecialityMenu";
import TopDoctors from "../../components/Home/TopDoctors";
import Banner from "../../components/Home/Banner";

const Home = () => {
  return (
    <div>
      <Header />
      <SpecialityMenu />
      <TopDoctors />
      <Banner />
    </div>
  );
};

export default Home;
