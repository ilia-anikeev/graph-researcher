import React, { createContext, useState} from "react"

export const UserContext = createContext();

export const UserProvider = ({children}) => {
    const [userID, setUserID] = useState(-1);

    const updateUserID = (newUserID) => {
        setUserID(newUserID);
    }

    return (
        <UserContext.Provider value={{userID, updateUserID}}>
            {children}
        </UserContext.Provider>
    );
}