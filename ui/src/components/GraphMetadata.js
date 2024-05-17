import React from "react";
import PropTypes from 'prop-types';
import "./GraphMetadata.css";
import "../index.css"

function GraphMetadata(props){
    const [isOpen, setState] = React.useState(false)

    return (
        <div>
            <button className="button" onClick={() => {
                setTimeout(() => setState(true), 1500); 
                props.setEdgeCreate(false)}
                }>      
                    Research      
            </button>
            {isOpen && <div className="GraphMetadata">
                <div className="GraphMetadata-body" >
                    <h1 style={{textAlign: "center"}}>Info</h1>
                    <p>Bridges                                                                     no </p>  
                    <p>Articulation Points                                                 3</p>
                    <p>Connected Components                              (1, 2, 3, 4, 5)</p>
                    <button style={{alignSelf: "right"}} onClick={() => setState(false)}>Close</button>
                </div>
            </div>
            }
        </div>
    )
}

export default GraphMetadata;

GraphMetadata.propTypes = {
    setEdgeCreate: PropTypes.func
}