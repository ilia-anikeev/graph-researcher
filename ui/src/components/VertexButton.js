import React, { useEffect, useRef } from "react";
import PropTypes from 'prop-types';
import "./VertexButton.css";

function VertexButton(props) {
    const buttonsRef = useRef([]);

    useEffect (() => {
        
    })
    const handleMouseMove = (id) => (event) => {
        const rect = event.target.getBoundingClientRect();
        props.func(id, rect.x, rect.y)
        console.log(rect.x, rect.y)
        props.buttonsInput.forEach(element => {
            console.log("id:", element.id, element.x, element.y)
        });
    };

    return (
        <div>
            {props.buttonsInput ? props.buttonsInput.map(button => {
                return (
                    <div 
                        key={button.id} 
                        ref={ref => buttonsRef.current[button.id] = ref}
                        onClick={handleMouseMove(button.id)}
                    >
                        {button.data}
                    </div>
                );
            }) : null}
        </div>
    );
}


VertexButton.propTypes = {
    buttonsInput: PropTypes.array,
    func: PropTypes.func
}

export default VertexButton;

