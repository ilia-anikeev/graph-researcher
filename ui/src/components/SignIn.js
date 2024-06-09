import React from 'react';
import './SignIn.css'
import { useNavigate } from "react-router-dom";

function SignIn(){
    const [email, setEmail] = React.useState('');
    const [password, setPassword] = React.useState('');
    const [isEmailEmpty, setIsEmailEmpty] = React.useState(false);
    const [isPasswordEmpty, setIsPasswordEmpty] = React.useState(false);

    const navigate = useNavigate();

    const signIn = () => {
        console.log(email);
        console.log(password);
        if (isEmpty()) {
             return;
        }
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
                        <input  onChange={e => setPassword(e.target.value)} type={'password'}/>
                    </label>
                </div>
                <button className='signInButton' onClick={signIn}>Sign in</button> <br/>
                <div className='errorTextSignIn'>
                    {(isEmailEmpty && <text>        Please, enter email</text>) || (isPasswordEmpty && <text>Please, enter password</text>)}
                </div>
                <button className='createAccountButton' onClick={goToSignUpPage}>Create account</button>            
            </div>
        </div>
    );
}

export default SignIn;