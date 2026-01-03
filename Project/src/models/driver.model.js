import mongoose from "mongoose";

const driverModel = new mongoose.Schema(
    {
        // Basic driver information
        name: {
            type: String,
            required:  true,
            trim: true,
        },
        lastName: {
            type: String,
            required: true,
            trim:  true,
        },
        email: {
            type: String,
            required: true,
            unique: true,
            trim:  true,
        },
        password: {
            type: String,
            required: true,
        },

        // Real-time current location
        currentTrip: {
            type: {
                type: String,
                enum: ["Point"],
                default: "Point",
            },
            coordinates:  {
                type: [mongoose. Schema.Types.Decimal128],
            },
        },
        currentLocation: {
            type:  {
                type: String,
                enum: ["Point"],
                default: "Point",
            },
            coordinates: {
                type: [mongoose.Schema.Types. Decimal128],
            },
        },

        // Driver status
        isOnline: {
            type: Boolean,
            default:  false,
        },

        // Rating and statistics
        rating: {
            average: {
                type: Number,
                min: 0,
                max: 5,
                default: 5,
            },
            totalRatings: {
                type: Number,
                default: 0,
            },
        },
        totalTrips: {
            type: Number,
            default: 0,
        },
        totalEarnings: {
            type: Number,
            default: 0,
        },
        subscription: {
            isSubscribed:  {
                type: Boolean,
                default: false,
            },
            lastPayment: {
                date: Date
            },
            subscriptionDuration: {
                type: Number,
            }
        },

        pets: {
            type:  Boolean,
            default: false,
        },

        // Number of the driver
        number: {
            type: String,
            trim: true,
            default: null,
        },

        // Vehicle information
        vehicle: {
            brand: {
                type: String,
                trim: true,
                default: null,
            },
            model: {
                type: String,
                trim:  true,
                default: null,
            },
            year: {
                type: Number,
            },
            licensePlate: {
                type: String,
                trim: true,
                default:  null,
            },
            capacity: {
                type: Number,
                default: 4,
            },
        },

        // Documentation and verification
        documents: {
            driverLicense: {
                number: String,
                expiryDate: Date,
                isVerified: {
                    type: Boolean,
                    default: false,
                },
            },
            vehicleRegistration: {
                number: String,
                expiryDate: Date,
                isVerified: {
                    type: Boolean,
                    default: false,
                },
            },
            insurance:  {
                number: String,
                expiryDate: Date,
                isVerified: {
                    type: Boolean,
                    default: false,
                },
            },
        },
    },
    {
        timestamps: true,
    }
);

// Geospatial index for proximity searches
driverModel.index({ currentLocation: "2dsphere" });

// Índices para consultas frecuentes
driverModel.index({ isOnline: 1 });

// Índices únicos pero sparse (permiten múltiples valores null/undefined)
driverModel.index({ number: 1 }, { unique:  true, sparse: true });
driverModel.index({ 'vehicle.brand': 1 }, { unique: true, sparse: true });
driverModel.index({ 'vehicle.model': 1 }, { unique: true, sparse:  true });
driverModel.index({ 'vehicle.licensePlate': 1 }, { unique:  true, sparse: true });

export default mongoose.model("Driver", driverModel);