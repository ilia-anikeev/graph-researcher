import React, { useState } from 'react';
import { useNavigate } from "react-router-dom";
import { Button, Input } from 'antd';
import './SignUp.css'

function SignUp(){
    const [email, setEmail] = useState('');
    const [password1, setPassword1] = useState('');
    const [password2, setPassword2] = useState('');
    const [isEmailEmpty, setIsEmailEmpty] = useState(false);
    const [isPassword1Empty, setIsPassword1Empty] = useState(false);
    const [isPassword2Empty, setIsPassword2Empty] = useState(false);
    const [isPasswordsDiffer, setIsPasswordsDiffer] = useState(false);
    const navigate = useNavigate();


    const signUp = () => {
        if (isEmpty()) {
            return;
        }
        if (password1 !== password2) {
            setIsPasswordsDiffer(true);
            return;
        }
        navigate('/');
    }


    const isEmpty = () => {
        if (email.length === 0){
            setIsEmailEmpty(true);
            return true;
        } else if (password1.length === 0) {
            setIsPassword1Empty(true);
            setIsEmailEmpty(false);
            return true;
        } else if (password2.length === 0){
            setIsPassword2Empty(true);
            setIsPassword1Empty(false);
            setIsEmailEmpty(false);
            return true;
        }
        setIsPassword2Empty(false);
        setIsPassword1Empty(false);
        setIsEmailEmpty(false);
        return false;
    }


    return(
        <div className='body'>
            <div className='signUpBody'>
                <div className='headderSignUp'> Sign up </div>
                    <div className='inputs'>
                        Login
                        <Input className='inputLogin' value={email} onChange={e => setEmail(e.target.value)}/>
                        Password
                        <Input.Password className='inputPassword' onChange={e => setPassword1(e.target.value)}/>
                        Repeat password
                        <Input.Password onChange={e => setPassword2(e.target.value)}/>
                        <div className='errorTextSignUp'>
                        {(isEmailEmpty && <text>        Please, enter email</text>) 
                            || (isPassword1Empty && <text>Please, enter password</text>) 
                            || (isPassword2Empty && <text>Please, repeat password</text>) 
                            || (isPasswordsDiffer && <text>Please, repeat password again</text>)}
                    </div>
                    <Button className='signUpButton' onClick={signUp} type='default'>Sign up</Button> <br/>
                </div>
            </div>
        </div>
    );
}

export default SignUp;