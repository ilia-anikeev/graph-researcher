import React, { createContext, useState } from 'react';


export const AppContext = createContext();


export const AppProvider = ({ children }) => {
    const [graphId, setGraphId] = useState(null);
    const [isSearched, setIsSearched] = useState(false);

    return (
        <AppContext.Provider value={{ graphId, setGraphId, isSearched, setIsSearched }}>
            {children}
        </AppContext.Provider>
    );
};
