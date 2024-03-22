import React from "react";
import PropTypes from 'prop-types';
import "./VertexButton.css"
import Vertex from "./Vertex";


function VertexButton(props){
    return (
        <div>
            {props.buttonsInput ? props.buttonsInput.map(button => {
                    // return <button className='vertexButton' key={button.id}>
                    //         click on me!!!
                    //        </button>
                    return <Vertex key={button.id}/>
                }) : null}
        </div>
    )
}

VertexButton.propTypes = {
    buttonsInput: PropTypes.array
}

export default VertexButton