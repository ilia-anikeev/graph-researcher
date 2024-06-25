import React, { useState, useContext } from 'react';
import { Button, Input } from 'antd';
import { useNavigate } from "react-router-dom";
import { UserContext } from './UserContex';
import './SignIn.css'

function SignIn(){
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [isEmailEmpty, setIsEmailEmpty] = useState(false);
    const [isPasswordEmpty, setIsPasswordEmpty] = useState(false);

    const { updateUserID } = useContext(UserContext);
    const navigate = useNavigate();


    const signIn = () => {
        if (isEmpty()) {
             return;
        }

        fetch('http://localhost:8080/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Connection': 'keep-alive'
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                setErrorMessage('Try again!');
            }
        })
        .then(id => {
            updateUserID(id.toString());
            navigate('/');
        })
        .catch(error => {

            console.log(error);
            setErrorMessage('Try again!');
        });
    }


    const isEmpty = () => {
        if (username.length === 0) {
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
                        <Input className='inputLogin' value={username} onChange={e => setUsername(e.target.value)}/>
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