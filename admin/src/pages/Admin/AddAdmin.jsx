import React from "react";
import { useAddAdmin } from "../../hooks/admin/addAdmin/useAddAdmin";
import AddAdminForm from "../../components/admin/addAdmin/AddAdminForm";

const AddAdmin = () => {
  const addAdminProps = useAddAdmin();

  return <AddAdminForm {...addAdminProps} />;
};

export default AddAdmin;
