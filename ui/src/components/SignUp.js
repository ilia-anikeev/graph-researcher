import React from 'react';
import './SignUp.css'
import { useNavigate } from "react-router-dom";

function SignUp(){
    const [email, setEmail] = React.useState('');
    const [password1, setPassword1] = React.useState('');
    const [password2, setPassword2] = React.useState('');
    const [isEmailEmpty, setIsEmailEmpty] = React.useState(false);
    const [isPassword1Empty, setIsPassword1Empty] = React.useState(false);
    const [isPassword2Empty, setIsPassword2Empty] = React.useState(false);
    const [isPasswordsDiffer, setIsPasswordsDiffer] = React.useState(false);
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
        <div>
            <div className='headderSignUp'>
                <p>Sign up</p> 
            </div>
            <div className='dataSignUp'>
                Email
                <div className='emailSignUp'>
                    <label>
                        <input value={email} onChange={e => setEmail(e.target.value)}/>
                    </label>
                </div>
                <div className='passwordSignUp'>
                    Password <br/>
                    <label>
                        <input onChange={e => setPassword1(e.target.value)} type={'password'}/> 
                    </label>
                </div>
                <div className='password'>
                    Repeat password <br/> 
                    <label>
                        <input onChange={e => setPassword2(e.target.value)} type={'password'}/>
                    </label>
                </div>
                <button className='signUpButton' onClick={signUp}>Sign up</button> 
                <div className='errorTextSignUp'>
                    {(isEmailEmpty && <text>        Please, enter email</text>) || (isPassword1Empty && <text>Please, enter password</text>) || (isPassword2Empty && <text>Please, repeat password</text>) || (isPasswordsDiffer && <text>Please, repeat password again</text>)}
                </div>
                <div>
                </div>
            </div>
        </div>
    );
}

export default SignUp;