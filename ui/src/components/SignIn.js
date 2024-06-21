import React, { useState, useContext } from 'react';
import { Button, Input } from 'antd';
import { useNavigate } from "react-router-dom";
import { UserContext } from './UserContex';
import './SignIn.css'

function SignIn(){
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [isEmailEmpty, setIsEmailEmpty] = useState(false);
    const [isPasswordEmpty, setIsPasswordEmpty] = useState(false);

    const { updateUserID} = useContext(UserContext);
    const navigate = useNavigate();


    const signIn = () => {
        if (isEmpty()) {
             return;
        }
        if (email !== '1' || password !== '1'){
            setErrorMessage('Invalid email or password');
            return;
        }
        updateUserID(0);
        navigate('/');
    }


    const isEmpty = () => {
        if (email.length === 0) {
            setIsEmailEmpty(true);
            return true;
        } else if (password.length === 0) {
            setIsPasswordEmpty(true);
            setIsEmailEmpty(false);
            return true;
        }
        return false;
    }


    const goToSignUpPage = () => {
        navigate('/signUp');
    }

    return(
        <div>
            <div className='body'>
                <div className='signInBody'>
                    <div className='headderSignIn'> Sign in </div>
                    <div className='inputs'>
                        Login
                        <Input className='inputLogin' value={email} onChange={e => setEmail(e.target.value)}/>
                        Password
                        <Input.Password onChange={e => {setPassword(e.target.value); setErrorMessage('')}}/>
                        <Button className='signInButton' onClick={signIn} type='default'>Sign in</Button> <br/>
                        <div className='errorTextSignIn'>
                            {(isEmailEmpty && <text>        Please, enter email</text>) || (isPasswordEmpty && <text>Please, enter password</text>)}
                            {(errorMessage !== '' && <text> {errorMessage} </text>)}
                        </div>
                        <Button className='createAccountButton' onClick={goToSignUpPage} type='link'>Create account</Button>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default SignIn;