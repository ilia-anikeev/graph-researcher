import React from "react";
import PropTypes from 'prop-types';
import "./VertexButton.css"
import Vertex from "./Vertex";


function VertexButton(props){
    return (
        <div>
            {props.vertices.map(vertex => {
                    return <Vertex key={vertex.id}/>
                })}
        </div>
    )
}

VertexButton.propTypes = {
    vertices: PropTypes.array
}

export default VertexButton