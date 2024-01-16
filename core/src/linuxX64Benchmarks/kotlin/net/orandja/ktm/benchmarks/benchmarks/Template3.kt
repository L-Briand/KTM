package net.orandja.ktm.benchmarks

import kotlinx.serialization.Serializable

@Serializable
data class _3_Context(
    val items: List<Item>,
) {
    @Serializable
    data class Item(
        val name: String,
        val price: Int,
        val features: List<String>
    )
}

val __3_template = """
{{#items}}
    Name: {{name}}
    Price: {{price}}
    {{#countries}}
        Country: {{.}}
    {{/countries}}
{{/items}}
""".trimIndent()

val __3_context = """
{
  "items": [
    {
      "name": "Item 0",
      "price": 784,
      "countries": [
        "Trinidad and Tobago",
        "Italy",
        "Rwanda"
      ]
    },
    {
      "name": "Item 1",
      "price": 9199,
      "countries": [
        "Marshall Islands"
      ]
    },
    {
      "name": "Item 2",
      "price": 2201,
      "countries": [
        "Angola",
        "Chile",
        "Latvia",
        "Ecuador",
        "Tuvalu",
        "Poland",
        "Marshall Islands",
        "Vanuatu",
        "Holy See",
        "Latvia"
      ]
    },
    {
      "name": "Item 3",
      "price": 5419,
      "countries": [
        "Brazil",
        "Ghana",
        "Comoros"
      ]
    },
    {
      "name": "Item 4",
      "price": 5480,
      "countries": [
        "Ireland",
        "Egypt"
      ]
    },
    {
      "name": "Item 5",
      "price": 3311,
      "countries": [
        "Germany",
        "Gambia, The"
      ]
    },
    {
      "name": "Item 6",
      "price": 4683,
      "countries": [
        "Kyrgyzstan",
        "Ethiopia",
        "El Salvador",
        "Sweden",
        "France",
        "Honduras",
        "Israel"
      ]
    },
    {
      "name": "Item 7",
      "price": 4821,
      "countries": [
        "Paraguay",
        "Bangladesh",
        "Cuba",
        "Spain"
      ]
    },
    {
      "name": "Item 8",
      "price": 378,
      "countries": [
        "Ecuador",
        "Macau"
      ]
    },
    {
      "name": "Item 9",
      "price": 7427,
      "countries": [
        "Venezuela",
        "Russia",
        "Switzerland"
      ]
    },
    {
      "name": "Item 10",
      "price": 397,
      "countries": [
        "Estonia",
        "Mongolia",
        "Suriname",
        "Costa Rica",
        "Argentina"
      ]
    },
    {
      "name": "Item 11",
      "price": 148,
      "countries": [
        "Zambia",
        "Tuvalu"
      ]
    },
    {
      "name": "Item 12",
      "price": 9476,
      "countries": [
        "Jamaica",
        "Bosnia and Herzegovina",
        "Eritrea",
        "Libya",
        "China",
        "Korea, South",
        "Rwanda",
        "Liberia",
        "Antigua and Barbuda"
      ]
    },
    {
      "name": "Item 13",
      "price": 634,
      "countries": [
        "Egypt",
        "Mali",
        "Tanzania",
        "Bhutan"
      ]
    },
    {
      "name": "Item 14",
      "price": 8342,
      "countries": [
        "Uzbekistan",
        "Indonesia",
        "Panama",
        "Kosovo",
        "Estonia",
        "Egypt",
        "Italy",
        "Korea, South",
        "Congo, Democratic Republic of the",
        "India"
      ]
    },
    {
      "name": "Item 15",
      "price": 5399,
      "countries": [
        "Australia",
        "Palestinian Territories",
        "Tanzania",
        "Ecuador",
        "Libya",
        "Trinidad and Tobago"
      ]
    },
    {
      "name": "Item 16",
      "price": 7732,
      "countries": [
        "Mongolia"
      ]
    },
    {
      "name": "Item 17",
      "price": 4108,
      "countries": [
        "Turkmenistan",
        "South Africa",
        "Sint Maarten",
        "Burundi",
        "Mauritania",
        "Kyrgyzstan",
        "Luxembourg",
        "Belize",
        "Japan",
        "Turkmenistan"
      ]
    },
    {
      "name": "Item 18",
      "price": 7318,
      "countries": [
        "Czech Republic",
        "Morocco",
        "Lithuania",
        "Monaco",
        "Chile",
        "Somalia",
        "Tanzania",
        "Antigua and Barbuda",
        "Czech Republic"
      ]
    },
    {
      "name": "Item 19",
      "price": 8477,
      "countries": [
        "Honduras",
        "South Africa",
        "Laos",
        "Lesotho",
        "Bahrain"
      ]
    },
    {
      "name": "Item 20",
      "price": 4159,
      "countries": [
        "Tonga",
        "Burkina Faso",
        "Namibia",
        "Malta",
        "Indonesia",
        "Macedonia",
        "Somalia"
      ]
    },
    {
      "name": "Item 21",
      "price": 9655,
      "countries": [
        "Austria",
        "Haiti",
        "Australia",
        "Guyana",
        "Qatar",
        "Cote d'Ivoire"
      ]
    },
    {
      "name": "Item 22",
      "price": 6174,
      "countries": [
        "Guinea",
        "Cambodia",
        "Sudan",
        "United Arab Emirates",
        "Nicaragua",
        "Palau",
        "Samoa"
      ]
    },
    {
      "name": "Item 23",
      "price": 4800,
      "countries": [
        "Latvia",
        "Saint Kitts and Nevis",
        "Haiti",
        "Kyrgyzstan",
        "Liberia",
        "Gambia, The"
      ]
    },
    {
      "name": "Item 24",
      "price": 5589,
      "countries": [
        "Chile",
        "Equatorial Guinea",
        "Cuba",
        "Cote d'Ivoire",
        "United Kingdom",
        "Norway",
        "Gabon",
        "Slovakia",
        "Russia"
      ]
    },
    {
      "name": "Item 25",
      "price": 2084,
      "countries": [
        "Chad",
        "Burkina Faso",
        "Montenegro",
        "Paraguay",
        "Brunei",
        "Gabon",
        "South Korea",
        "Liberia",
        "India"
      ]
    },
    {
      "name": "Item 26",
      "price": 7306,
      "countries": [
        "Palestinian Territories",
        "Cuba",
        "Aruba"
      ]
    },
    {
      "name": "Item 27",
      "price": 1020,
      "countries": [
        "Cuba",
        "Micronesia",
        "Trinidad and Tobago",
        "Japan",
        "Tanzania",
        "Philippines",
        "Guinea"
      ]
    },
    {
      "name": "Item 28",
      "price": 3564,
      "countries": [
        "Cameroon",
        "Sri Lanka",
        "Macau",
        "Maldives",
        "Libya",
        "Central African Republic"
      ]
    },
    {
      "name": "Item 29",
      "price": 4206,
      "countries": [
        "Venezuela",
        "Albania",
        "Cyprus",
        "Georgia",
        "Indonesia",
        "Iceland"
      ]
    },
    {
      "name": "Item 30",
      "price": 1462,
      "countries": [
        "Bahrain",
        "Peru",
        "Ukraine"
      ]
    },
    {
      "name": "Item 31",
      "price": 1882,
      "countries": [
        "Latvia",
        "Kyrgyzstan",
        "Togo",
        "Greece",
        "Libya",
        "Philippines",
        "Italy",
        "Azerbaijan"
      ]
    },
    {
      "name": "Item 32",
      "price": 5387,
      "countries": [
        "Honduras",
        "Niger",
        "Croatia"
      ]
    },
    {
      "name": "Item 33",
      "price": 2546,
      "countries": [
        "Bhutan",
        "Botswana",
        "Belize",
        "Costa Rica",
        "Lithuania",
        "Gabon",
        "Honduras",
        "Denmark",
        "Nicaragua",
        "Panama"
      ]
    },
    {
      "name": "Item 34",
      "price": 7772,
      "countries": [
        "Palau",
        "Ghana",
        "Mauritania",
        "Greece",
        "Japan",
        "Poland",
        "Mauritania"
      ]
    },
    {
      "name": "Item 35",
      "price": 8952,
      "countries": [
        "Cote d'Ivoire",
        "Czech Republic",
        "Uganda",
        "Venezuela",
        "Belarus",
        "Jamaica"
      ]
    },
    {
      "name": "Item 36",
      "price": 8481,
      "countries": [
        "Belgium",
        "Saint Lucia",
        "Congo, Republic of the",
        "Qatar",
        "Lebanon",
        "Jamaica",
        "Argentina",
        "Nicaragua"
      ]
    },
    {
      "name": "Item 37",
      "price": 2360,
      "countries": [
        "Germany",
        "Ghana",
        "Angola",
        "United Arab Emirates",
        "Latvia"
      ]
    },
    {
      "name": "Item 38",
      "price": 4652,
      "countries": [
        "Afghanistan",
        "Jordan",
        "Serbia"
      ]
    },
    {
      "name": "Item 39",
      "price": 3536,
      "countries": [
        "Argentina"
      ]
    },
    {
      "name": "Item 40",
      "price": 74,
      "countries": [
        "Spain",
        "Ireland",
        "Sweden",
        "Saint Vincent and the Grenadines",
        "Azerbaijan",
        "Mexico"
      ]
    },
    {
      "name": "Item 41",
      "price": 2615,
      "countries": [
        "Egypt",
        "Korea, South",
        "Serbia"
      ]
    },
    {
      "name": "Item 42",
      "price": 9548,
      "countries": [
        "Comoros"
      ]
    },
    {
      "name": "Item 43",
      "price": 5982,
      "countries": [
        "Kazakhstan",
        "Burundi",
        "Greece",
        "Netherlands Antilles",
        "Malawi",
        "Ecuador",
        "Lesotho",
        "Sweden",
        "Kosovo"
      ]
    },
    {
      "name": "Item 44",
      "price": 8445,
      "countries": [
        "Kenya",
        "Tanzania"
      ]
    },
    {
      "name": "Item 45",
      "price": 6036,
      "countries": [
        "Malaysia",
        "Bosnia and Herzegovina",
        "South Africa",
        "Saint Lucia",
        "Uzbekistan",
        "Chad",
        "Belize"
      ]
    },
    {
      "name": "Item 46",
      "price": 7544,
      "countries": [
        "China",
        "Chile",
        "Guinea",
        "Tunisia",
        "Italy",
        "Nigeria",
        "Montenegro",
        "Guyana"
      ]
    },
    {
      "name": "Item 47",
      "price": 4506,
      "countries": [
        "Haiti",
        "Thailand",
        "Palestinian Territories",
        "Ghana",
        "Uzbekistan",
        "Mexico"
      ]
    },
    {
      "name": "Item 48",
      "price": 5942,
      "countries": [
        "Afghanistan",
        "Mozambique",
        "Kiribati",
        "Finland",
        "Tonga"
      ]
    },
    {
      "name": "Item 49",
      "price": 731,
      "countries": [
        "Kazakhstan",
        "Namibia",
        "Romania",
        "Saint Kitts and Nevis",
        "East Timor (see Timor-Leste)",
        "Netherlands",
        "Costa Rica"
      ]
    },
    {
      "name": "Item 50",
      "price": 3731,
      "countries": [
        "Trinidad and Tobago"
      ]
    },
    {
      "name": "Item 51",
      "price": 4044,
      "countries": [
        "Ethiopia",
        "United Kingdom",
        "Liberia"
      ]
    },
    {
      "name": "Item 52",
      "price": 5358,
      "countries": [
        "Taiwan",
        "Hungary",
        "Seychelles",
        "Cameroon",
        "Belize",
        "Seychelles",
        "El Salvador",
        "Cyprus"
      ]
    },
    {
      "name": "Item 53",
      "price": 3603,
      "countries": [
        "Romania",
        "Andorra",
        "Mexico"
      ]
    },
    {
      "name": "Item 54",
      "price": 9542,
      "countries": [
        "Belarus",
        "Japan",
        "Jordan",
        "Saint Vincent and the Grenadines",
        "Tajikistan",
        "Finland",
        "El Salvador",
        "Latvia",
        "Sweden",
        "El Salvador"
      ]
    },
    {
      "name": "Item 55",
      "price": 9112,
      "countries": [
        "Japan",
        "Lesotho",
        "Bolivia",
        "Nepal",
        "France"
      ]
    },
    {
      "name": "Item 56",
      "price": 2210,
      "countries": [
        "Guinea-Bissau",
        "Iceland"
      ]
    },
    {
      "name": "Item 57",
      "price": 5713,
      "countries": [
        "Palestinian Territories",
        "Mali"
      ]
    },
    {
      "name": "Item 58",
      "price": 8989,
      "countries": [
        "Zimbabwe",
        "Panama",
        "Gabon",
        "Panama"
      ]
    },
    {
      "name": "Item 59",
      "price": 4931,
      "countries": [
        "Macedonia",
        "Kuwait",
        "Malaysia",
        "Angola",
        "Ecuador",
        "Botswana"
      ]
    },
    {
      "name": "Item 60",
      "price": 3091,
      "countries": [
        "Laos",
        "Brunei",
        "Samoa"
      ]
    },
    {
      "name": "Item 61",
      "price": 7317,
      "countries": [
        "Jamaica",
        "Kazakhstan",
        "Burma",
        "Saint Lucia",
        "Suriname",
        "Guinea-Bissau",
        "Brazil",
        "Portugal",
        "Sudan",
        "Nauru"
      ]
    },
    {
      "name": "Item 62",
      "price": 1675,
      "countries": [
        "Kiribati"
      ]
    },
    {
      "name": "Item 63",
      "price": 7690,
      "countries": [
        "Niger",
        "Malta"
      ]
    },
    {
      "name": "Item 64",
      "price": 7174,
      "countries": [
        "Malawi",
        "Nicaragua",
        "Albania",
        "Italy",
        "Timor-Leste",
        "Trinidad and Tobago",
        "Portugal"
      ]
    },
    {
      "name": "Item 65",
      "price": 4438,
      "countries": [
        "Belize",
        "Sudan",
        "Mali",
        "United Kingdom",
        "Qatar"
      ]
    },
    {
      "name": "Item 66",
      "price": 8420,
      "countries": [
        "Singapore",
        "Macedonia",
        "San Marino",
        "Saint Kitts and Nevis",
        "Taiwan",
        "Australia",
        "Singapore",
        "Pakistan"
      ]
    },
    {
      "name": "Item 67",
      "price": 3335,
      "countries": [
        "Austria",
        "Seychelles",
        "Canada"
      ]
    },
    {
      "name": "Item 68",
      "price": 3381,
      "countries": [
        "Moldova",
        "New Zealand",
        "Palestinian Territories",
        "Australia"
      ]
    },
    {
      "name": "Item 69",
      "price": 6958,
      "countries": [
        "San Marino",
        "Zimbabwe",
        "Portugal",
        "France",
        "Malta",
        "Burundi"
      ]
    },
    {
      "name": "Item 70",
      "price": 6831,
      "countries": [
        "Pakistan",
        "Serbia",
        "Rwanda",
        "Jordan"
      ]
    },
    {
      "name": "Item 71",
      "price": 3130,
      "countries": [
        "Philippines",
        "Yemen"
      ]
    },
    {
      "name": "Item 72",
      "price": 6287,
      "countries": [
        "Ethiopia",
        "Burma"
      ]
    },
    {
      "name": "Item 73",
      "price": 9475,
      "countries": [
        "Mauritania",
        "Rwanda"
      ]
    },
    {
      "name": "Item 74",
      "price": 1575,
      "countries": [
        "South Africa",
        "Chile",
        "East Timor (see Timor-Leste)",
        "Ireland",
        "Congo, Democratic Republic of the",
        "Estonia",
        "Burma",
        "Suriname"
      ]
    },
    {
      "name": "Item 75",
      "price": 1609,
      "countries": [
        "Solomon Islands",
        "Bangladesh",
        "Hong Kong",
        "Senegal",
        "Sweden",
        "Mexico",
        "Jamaica",
        "Laos",
        "Netherlands"
      ]
    },
    {
      "name": "Item 76",
      "price": 858,
      "countries": [
        "Cuba",
        "Chad",
        "Lithuania",
        "Indonesia",
        "Timor-Leste",
        "Sudan",
        "Laos"
      ]
    },
    {
      "name": "Item 77",
      "price": 1491,
      "countries": [
        "Sao Tome and Principe"
      ]
    },
    {
      "name": "Item 78",
      "price": 6657,
      "countries": [
        "Lebanon",
        "Burma",
        "Rwanda",
        "Cote d'Ivoire",
        "Argentina",
        "Monaco",
        "Nigeria",
        "Luxembourg"
      ]
    },
    {
      "name": "Item 79",
      "price": 2943,
      "countries": [
        "Kiribati",
        "Gambia, The",
        "Ukraine",
        "Colombia",
        "Czech Republic",
        "Kiribati"
      ]
    },
    {
      "name": "Item 80",
      "price": 9222,
      "countries": [
        "Rwanda",
        "Bangladesh",
        "Slovenia",
        "Sweden",
        "Madagascar",
        "Luxembourg"
      ]
    },
    {
      "name": "Item 81",
      "price": 152,
      "countries": [
        "Jordan",
        "Andorra",
        "Peru"
      ]
    },
    {
      "name": "Item 82",
      "price": 9259,
      "countries": [
        "Swaziland",
        "Turkey",
        "Croatia",
        "Argentina",
        "Maldives",
        "Guinea",
        "Paraguay",
        "Yemen",
        "Belize",
        "Slovakia"
      ]
    },
    {
      "name": "Item 83",
      "price": 2446,
      "countries": [
        "Kosovo",
        "Botswana",
        "Sri Lanka",
        "Palau"
      ]
    },
    {
      "name": "Item 84",
      "price": 5076,
      "countries": [
        "Israel",
        "Yemen",
        "Poland"
      ]
    },
    {
      "name": "Item 85",
      "price": 9055,
      "countries": [
        "Sao Tome and Principe",
        "Mozambique",
        "France",
        "Venezuela",
        "Korea, South",
        "Czech Republic",
        "Venezuela",
        "Saint Kitts and Nevis",
        "Sao Tome and Principe"
      ]
    },
    {
      "name": "Item 86",
      "price": 1784,
      "countries": [
        "Kosovo"
      ]
    },
    {
      "name": "Item 87",
      "price": 9553,
      "countries": [
        "Equatorial Guinea",
        "Zimbabwe",
        "Palau",
        "Mozambique"
      ]
    },
    {
      "name": "Item 88",
      "price": 2965,
      "countries": [
        "Niger"
      ]
    },
    {
      "name": "Item 89",
      "price": 4317,
      "countries": [
        "Dominican Republic"
      ]
    },
    {
      "name": "Item 90",
      "price": 1576,
      "countries": [
        "Malawi",
        "Mali",
        "Sierra Leone",
        "Honduras",
        "Cameroon",
        "Turkmenistan",
        "Djibouti",
        "France"
      ]
    },
    {
      "name": "Item 91",
      "price": 3044,
      "countries": [
        "Czech Republic",
        "Marshall Islands",
        "Fiji",
        "Sri Lanka",
        "Macau",
        "Timor-Leste"
      ]
    },
    {
      "name": "Item 92",
      "price": 6121,
      "countries": [
        "Luxembourg",
        "Malta",
        "Australia",
        "Bolivia",
        "Serbia",
        "Congo, Republic of the",
        "Colombia"
      ]
    },
    {
      "name": "Item 93",
      "price": 9023,
      "countries": [
        "Angola",
        "Portugal",
        "Canada",
        "Georgia",
        "Mozambique",
        "Benin",
        "Algeria",
        "South Korea",
        "Burundi"
      ]
    },
    {
      "name": "Item 94",
      "price": 9827,
      "countries": [
        "Bulgaria",
        "Haiti",
        "Italy",
        "Burma",
        "Botswana",
        "Finland",
        "Sudan",
        "Sri Lanka"
      ]
    },
    {
      "name": "Item 95",
      "price": 7272,
      "countries": [
        "Grenada"
      ]
    },
    {
      "name": "Item 96",
      "price": 5950,
      "countries": [
        "Gambia, The",
        "Rwanda",
        "Papua New Guinea",
        "Latvia",
        "Mongolia"
      ]
    },
    {
      "name": "Item 97",
      "price": 7051,
      "countries": [
        "Chile",
        "Libya",
        "Australia",
        "El Salvador",
        "Bangladesh",
        "Niger",
        "Kenya",
        "Liechtenstein",
        "Madagascar",
        "Kosovo"
      ]
    },
    {
      "name": "Item 98",
      "price": 252,
      "countries": [
        "Korea, South",
        "Monaco",
        "Djibouti",
        "Tunisia"
      ]
    },
    {
      "name": "Item 99",
      "price": 933,
      "countries": [
        "Andorra"
      ]
    },
    {
      "name": "Item 100",
      "price": 9708,
      "countries": [
        "Hungary",
        "Singapore",
        "Guinea",
        "Kuwait",
        "Bangladesh",
        "Bulgaria",
        "Armenia"
      ]
    }
  ]
}
""".trimIndent()