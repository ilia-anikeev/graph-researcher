import React, { useRef } from "react";
import PropTypes from 'prop-types';
import "./VertexButton.css";

function VertexButton(props) {
    const verticesRef = useRef([]);

    const handleMouseMove = (id) => (event) => {
        const rect = event.target.getBoundingClientRect();
        props.updateButtonCoordinates(id, rect.x, rect.y)
    };

    return (
        <div>
            {props.vertices ? props.vertices.map(vertex => {
                return (
                    <div 
                        key={vertex.id} 
                        ref={ref => verticesRef.current[vertex.id] = ref}
                        onClick={handleMouseMove(vertex.id)}
                    >
                        {vertex.vertex}
                    </div>
                );
            }) : null}  
        </div>
    );
}


VertexButton.propTypes = {
    vertices: PropTypes.array,
    updateButtonCoordinates: PropTypes.func
}

export default VertexButton;

