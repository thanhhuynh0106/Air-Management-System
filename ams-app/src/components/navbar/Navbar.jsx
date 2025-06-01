import React, {useState} from "react";

import { SiConsul } from "react-icons/si";
import { BsPhone } from "react-icons/bs";
import { BsPhoneVibrate } from "react-icons/bs";
import { AiOutlineGlobal } from "react-icons/ai";
import { CgMenuGridO } from "react-icons/cg";

import logo from "../../assets/logo.png";
import { Link } from "react-router-dom";

const Navbar = () => {

    const [active, setActive] = useState(false);

    const toggleNavBar = () => {
        setActive(!active);
    };

    const [noBg, addBg] = useState('navBarTwo');
    const addBgColor = () => {
        if (window.scrollY > 10) {
            addBg('navBarTwo navBarWithBg');
        }
        else {
            addBg('navBarTwo');
        }
    };
    window.addEventListener('scroll', addBgColor); 


    return (
        <div className="navBar flex">

             <div className="navBarOne flex">
                <div>
                    <SiConsul className="icon"/>
                </div>
                <div className="none flex">
                    <li className="flex"> <BsPhoneVibrate className="icon"/> Support</li>
                    <li className="flex"> <AiOutlineGlobal className="icon"/> Languages</li>
                </div>
                <div className="atb flex">
                    <span><Link to='/sign-in'>Sign In</Link></span>
                    <span><Link to='/sign-up'>Sign Up</Link></span>
                </div>
             </div>


             <div className={noBg}>

                <div className="logoDiv">
                    <img src={logo} className="logo" />
                </div>

                <div className={`navBarMenu ${active ? "showNavBar" : ""}`}>
                    <ul className="menu flex">
                        <li className="listItem">
                            <Link to='/'>Home</Link>
                        </li>
                        <li className="listItem">
                            <Link to='/search'>Search</Link>
                        </li>
                        <li className="listItem">
                            <Link to='/about'>About</Link>
                        </li>
                        <li className="listItem">
                            <Link to='/explore'>Explore</Link>
                        </li>
                    </ul>

                    <button className="btn flex btnOne">Contact</button>
                </div>

                <button className="btn flex btnTwo">Contact</button>

                <div onClick={toggleNavBar} className="toggleIcon">
                    <CgMenuGridO className="icon"/>
                </div>

            </div>
        </div>
    )
}

export default Navbar;