import React from "react";
import Header from "./header";
import { Outlet } from "react-router-dom";
const MainHeader=()=>{
        return(
            <>
           <Header/>
           <Outlet />
           </>
        )
}
export default MainHeader;