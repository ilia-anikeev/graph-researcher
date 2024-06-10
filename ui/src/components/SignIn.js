import React, { useState, useContext } from 'react';
import './SignIn.css'
import { useNavigate } from "react-router-dom";
import { UserContext } from './UserContex';

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
        navigate('/signup');
    }

    return(
        <div>
            <div className='headderSignIn'>
                <p>Sign in</p> 
            </div>
            <div className='dataSignIn'>
                Email  
                <div className='emailSignIn'>
                    <label>
                        <input value={email} onChange={e => setEmail(e.target.value)}/>
                    </label>
                </div>
                Password
                <div className='passwordSignIn'>
                    <label>
                        <input  onChange={e => {setPassword(e.target.value); setErrorMessage('')}} type={'password'}/>
                    </label>
                </div>
                <button className='signInButton' onClick={signIn}>Sign in</button> <br/>
                <div className='errorTextSignIn'>
                    {(isEmailEmpty && <text>        Please, enter email</text>) || (isPasswordEmpty && <text>Please, enter password</text>)}
                    {(errorMessage !== '' && <text> {errorMessage} </text>)}
                </div>
                <button className='createAccountButton' onClick={goToSignUpPage}>Create account</button>            
            </div>
        </div>
    );
}

export default SignIn;